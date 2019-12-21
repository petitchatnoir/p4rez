//
//  struct.c
//  mysh
//
//  Created by Hugo on 26/10/2019.
//  Copyright © 2019 Hugo. All rights reserved.
//

#include "struct.h"

Client create_client(void){
    Client c;
    struct sockaddr_in *addr;
    socklen_t *addr_sz;
    
    c = (Client)malloc(sizeof(client));
    addr = (struct sockaddr_in*)malloc(sizeof(struct sockaddr_in));
    addr_sz = (socklen_t*)malloc(sizeof(socklen_t));
    *addr_sz = sizeof(*addr);
    memset(addr, 0, *addr_sz);

    c->addr = addr;
    c->addr_sz = addr_sz;
    c->fd = 0;
    
    return c;
}

void free_client(Client c){
    if(c == NULL)
        return;
    if(c->addr != NULL)
        free(c->addr);
    if(c->addr_sz != NULL)
        free(c->addr_sz);
    
    if(close(c->fd) == ERR)
        syserror(12);
    
    free(c);
}

Game create_game(pid_t pid, Client p1, Client p2){
    Game g;
    
    g = (Game)malloc(sizeof(game));
    g->pid = pid;
    g->player1 = p1;
    g->player2 = p2;
    
    return g;
}

void free_game(Game g){
    if(g == NULL)
        return;
    if(g->player1 != NULL)
        free_client(g->player1);
    if(g->player2 != NULL)
        free_client(g->player2);
    free(g);
}

Packet create_packet(){
    Packet p;
    
    p = (Packet)malloc(sizeof(packet));
    p->str = (char*)malloc(MAX_STR_SZ * sizeof(char));
    p->win_tkn = NULL;
    
    return p;
}

void free_packet(Packet p){
    if(p == NULL)
        return;
    if(p->str != NULL)
        free(p->str);
    if(p->win_tkn != NULL)
        free(p->win_tkn);
}

void fill_packet(Packet p, char *buffer, size_t sz){
    int i, c;
    
    if(buffer[0] != PCKT_DELIM_TKN){
        p->type = PCKT_ERR;
        return;
    }
    
    p->type = buffer[1];
    
    switch (p->type) {
        case PCKT_PLACEX:
            p->x =  buffer[2];
            break;
        case PCKT_NAME:
            for(i = 0; i < MAX_STR_SZ; i++){
                c = (int)buffer[i + 2];
                if(c == PCKT_DELIM_TKN)
                    break;
                p->str[i] = (char)c;
            }
            p->str[i] = '\0';
            break;
        case PCKT_COLOR:
            p->x = buffer[2];
            p->y = buffer[3];
            p->z = buffer[4];
            break;
        case PCKT_REPLAY:
            break;
        default:
            p->type = PCKT_ERR;
    }
}

size_t fill_buffer(char *buffer, Packet p){
    size_t sz;
    int i;
    
    memset(buffer, '\0', PCKT_MAX_SZ);
    sz = 0;
    
    buffer[0] = PCKT_DELIM_TKN;
    buffer[1] = p->type;
    
    switch (p->type) {
        case PCKT_INI:
            buffer[2] = p->x;
            buffer[3] = PCKT_DELIM_TKN;
            sz = 4;
            break;
        case PCKT_PLACEXY:
            buffer[2] = p->x;
            buffer[3] = p->y;
            buffer[4] = PCKT_DELIM_TKN;
            sz = 5;
            break;
        case PCKT_NAME:
            for(i = 2; p->str[i - 2] != '\0'; i++)
                buffer[i] = p->str[i - 2];
            buffer[i] = PCKT_DELIM_TKN;
            sz = 3 + i;
            break;
        case PCKT_COLOR:
            buffer[2] = p->x;
            buffer[3] = p->y;
            buffer[4] = p->z;
            buffer[5] = PCKT_DELIM_TKN;
            sz = 6;
            break;
        case PCKT_WIN:
            buffer[2] = p->x;
            for(i = 0; i < 4; i++){
                buffer[2*i+3] = p->win_tkn[2*i];
                buffer[2*i+4] = p->win_tkn[2*i+1];
            }
            buffer[2*i+3] = PCKT_DELIM_TKN;
            sz = 2*i+3;
            break;
        case PCKT_DRAW:
            buffer[2] = PCKT_DELIM_TKN;
            sz = 3;
            break;
        case PCKT_CLIENT_QUIT:
            buffer[2] = PCKT_DELIM_TKN;
            sz = 3;
            break;
        case PCKT_REPLAY:
            buffer[2] = PCKT_DELIM_TKN;
            sz = 3;
            break;
        default:
            return 0;
    }

    return sz;
}
