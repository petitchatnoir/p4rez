//
//  game_server.c
//  p4rezServer
//
//  Created by Hugo on 09/11/2019.
//  Copyright © 2019 Hugo. All rights reserved.
//

#include "game_server.h"

void run_server(void){
    Client p1, p2;
    
    init_server();
    
    for(p1 = NULL, p2 = NULL;;){
        if(p1 == NULL)
            p1 = accept_new_client();
        else{
            p2 = accept_new_client();
            
            add_bckgnd_game(p1, p2);
            p1 = NULL;
            p2 = NULL;
        }
    }
}

void init_server(void){
    int cpt;
    
    create_server_socket();
    
    for(cpt = 0; cpt < MAX_GAME; cpt++)
        game_arr[cpt] = NULL;
    
    nb_game = 0;
    
    //assign signal handling functions
    if(signal(SIGCHLD, chld_hdlr) == SIG_ERR)
        syserror(8);
    if(signal(SIGINT, kill_server) == SIG_ERR)
        syserror(8);
    
    //launch discover daemon
    if((dscvr_daemon_pid = fork()) == ERR)
        syserror(5);
    
    if(!dscvr_daemon_pid){
        //assign different signal handling functions
        if(signal(SIGCHLD, SIG_DFL) == SIG_ERR)
            syserror(8);
        if(signal(SIGINT, kill_dscvr_daemon) == SIG_ERR)
            syserror(8);
        
        run_dscvr_daemon();
        exit(0);
    }
    
    printf("dscvr_daemon_pid : %d\n", dscvr_daemon_pid);
}

void create_server_socket(void){
    struct sockaddr_in addr;
    
    //create tcp socket
    if((srvr_socket = socket(AF_INET, SOCK_STREAM, 0)) == ERR)
        syserror(1);
    
    //init sockaddr struct
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = INADDR_ANY;
    addr.sin_port = htons(PORT);
    
    //assign address specified by addr to the socket
    if(bind(srvr_socket, (struct sockaddr *)&addr, sizeof(addr)) == ERR)
        syserror(2);
    
    //mark as a passive socket, used to accept incoming Clients
    if(listen(srvr_socket, MAX_PENDING) == ERR)
        syserror(10);
}

Client accept_new_client(void){
    Client c;
    
    c = create_client();
    
    //accept first request of pending connections & assign Client file descriptor to that socket.
    if((c->fd = accept(srvr_socket, (struct sockaddr*)c->addr, c->addr_sz)) == ERR)
        syserror(11);
    
    printf("new client connected : %s\n", inet_ntoa(c->addr->sin_addr));
    
    return c;
}

size_t rcv(Client client, Packet p){
    char buffer[PCKT_MAX_SZ];
    size_t sz;
    
    memset(buffer, 0, PCKT_MAX_SZ);
    
    //read bytes from Client into buffer
    if((sz = read(client->fd, buffer, PCKT_MAX_SZ)) == ERR)
        syserror(14);
    
    //fill converted data from buffer into Packet
    fill_packet(p, buffer, sz);
    
    return sz;
}

size_t snd(Client client, Packet p){
    char buffer[PCKT_MAX_SZ];
    size_t sz;
    
    //convert & store Packet data into buffer
    sz = fill_buffer(buffer, p);
    
    if(!sz)
        return 0;
    
    if((sz = write(client->fd, buffer, sz)) == ERR)
        syserror(13);
    
    return sz;
}

int add_bckgnd_game(Client p1, Client p2){
    pid_t pid;
    int cpt;
    
    //look for available slot in game_arr
    for(cpt = 0; cpt < MAX_GAME; cpt++){
        if(game_arr[cpt] == NULL)
            break;
    }
    
    if(cpt == MAX_GAME)
        return 0;
    
    //create a process for game
    if((pid = fork()) == ERR)
        syserror(5);
    
    if(!pid){
        //assign signal handling functions
        if(signal(SIGCHLD, SIG_DFL) == SIG_ERR)
            syserror(8);
        if(signal(SIGINT, kill_game) == SIG_ERR)
            syserror(8);
        
        run_game(p1, p2);
        exit(0);
    }
    
    nb_game++;
    game_arr[cpt] = create_game(pid, p1, p2);
    
    return 1;
}

void kill_server(int sig){
    int cpt;
    
    for(cpt = 0; cpt < MAX_GAME; cpt++){
        if(game_arr[cpt] != NULL)
            free_game(game_arr[cpt]);
    }
    
    if(close(srvr_socket) == ERR)
        syserror(12);
    
    printf("kill game server\n");
    exit(0);
}

void chld_hdlr(int sig){
    pid_t pid;
    int status;
    int cpt;
    
    while((pid = waitpid(-1, &status, WNOHANG)) > 0){
        for(cpt = 0; cpt < MAX_GAME; cpt++){
            if(game_arr[cpt] != NULL && pid == game_arr[cpt]->pid)
                break;
        }
        if(cpt == MAX_GAME){
            if(pid == dscvr_daemon_pid)
                printf("dscvr_daemon unexpected exit\n");
            else
                printf("unknown child (pid = %d) exit\n", pid);
            print_status(status);
            exit(1);
        } else{
            printf("Fin de partie entre : %s ", inet_ntoa(game_arr[cpt]->player1->addr->sin_addr));
            printf("& %s\n", inet_ntoa(game_arr[cpt]->player2->addr->sin_addr));
            nb_game--;
            free_game(game_arr[cpt]);
            game_arr[cpt] = NULL;
        }
        print_status(status);
    }
}
