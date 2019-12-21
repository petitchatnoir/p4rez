//
//  game.c
//  p4rezServer
//
//  Created by Hugo on 11/11/2019.
//  Copyright © 2019 Hugo. All rights reserved.
//

#include "game.h"

void run_game(Client player1, Client player2){
    int cpt, l, c, *win_tkn, cpt_tkn;
    
    printf("Début de partie entre : %s ", inet_ntoa(player1->addr->sin_addr));
    printf("& %s\n", inet_ntoa(player2->addr->sin_addr));
    
    init_grid();
    pckt = create_packet();

    players[0] = player1;
    players[1] = player2;
    
    //exchange name & color between players
    for(cpt = 0; cpt < 4; cpt++){
        if(!rcv(players[cpt % 2], pckt))
            send_client_quit(players[nxt(cpt)]);
        else
            snd(players[nxt(cpt)], pckt);
    }
    
    srand((unsigned int)time(0));
    
    //replay game loop
    for(cpt = (rand() % 2 == 0);;){
        send_ini(cpt); //players[cpt] begin
        
        //game loop
        for(cpt_tkn = 0;;cpt = nxt(cpt)){
            //player left
            if(!rcv(players[cpt], pckt))
                send_client_quit(players[nxt(cpt)]); //tell to opponent & kill game
            
            if(pckt->type == PCKT_PLACEX){
                c = pckt->x;
                l = insert(c, cpt);
                cpt_tkn++; //incr nb token inserted
                
                send_place_token(l, c);
                
                if((win_tkn = victory(l, c)) != NULL){
                    send_win(cpt, win_tkn);
                    break;
                } else if(cpt_tkn == NB_LINE*NB_COL){
                    send_draw();
                    break;
                }
            }
        }
        //game terminated, replay?
        replay();
    }
}

void replay(){
    //wait replay request, if player disconnect tell to opponent
    if(!rcv(players[0], pckt))
        send_client_quit(players[1]);
    
    if(!rcv(players[1], pckt))
        send_client_quit(players[0]);
    
    snd(players[0], pckt);
    snd(players[1], pckt);
    
    //both want to replay, reinit game
    init_grid();
}

void send_ini(int num){
    pckt->type = PCKT_INI;
    pckt->x = 0; //index 0 will begin on players[num] side
    snd(players[num], pckt); //begin
    pckt->x = 1; //index 1 will begin on otherside
    snd(players[nxt(num)], pckt);
}

void send_client_quit(Client p){
    pckt->type = PCKT_CLIENT_QUIT;
    snd(p, pckt);
    kill_game(0);
}

void send_place_token(int l, int c){
    pckt->type = PCKT_PLACEXY;
    pckt->x = l;
    pckt->y = c;
    snd(players[0], pckt);
    snd(players[1], pckt);
}

void send_win(int winner, int *win_tkn){
    pckt->type = PCKT_WIN;
    pckt->win_tkn = win_tkn;
    pckt->x = 1; //boolean winner
    snd(players[winner], pckt);
    pckt->x = 0; //boolean loser
    snd(players[nxt(winner)], pckt);
}

void send_draw(){
    pckt->type = PCKT_DRAW;
    snd(players[0], pckt);
    snd(players[1], pckt);
}

void kill_game(int sig){
    free_packet(pckt);
    exit(sig);
}
