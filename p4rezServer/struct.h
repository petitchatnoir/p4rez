//
//  struct.h
//  mysh
//
//  Created by Hugo on 26/10/2019.
//  Copyright © 2019 Hugo. All rights reserved.
//

#ifndef struct_h
#define struct_h

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <netinet/in.h>
#include "error.h"

#define PCKT_MAX_SZ 64
#define MAX_STR_SZ (PCKT_MAX_SZ/4)-1
#define PCKT_DELIM_TKN '$'

enum pckt_type {PCKT_INI, PCKT_COLOR, PCKT_NAME, PCKT_PLACEX, PCKT_PLACEXY, PCKT_WIN, PCKT_ERR, PCKT_DRAW, PCKT_CLIENT_QUIT, PCKT_REPLAY};

typedef struct{
    int fd;
    struct sockaddr_in *addr;
    socklen_t *addr_sz;
} client;
typedef client* Client;

typedef struct{
    pid_t pid;
    Client player1;
    Client player2;
} game;
typedef game* Game;

typedef struct{
    enum pckt_type type;
    char *str;
    int x, y, z, *win_tkn;
} packet;
typedef packet* Packet;

Client create_client(void);

void free_client(Client);

Game create_game(pid_t,Client,Client);

void free_game(Game);

Packet create_packet(void);

void free_packet(Packet);

void fill_packet(Packet,char*,size_t);

size_t fill_buffer(char*,Packet);

#endif /* struct_h */
