//
//  game.h
//  p4rezServer
//
//  Created by Hugo on 11/11/2019.
//  Copyright © 2019 Hugo. All rights reserved.
//

#ifndef game_h
#define game_h

#include <stdio.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <string.h>
#include <time.h>
#include "struct.h"
#include "game_server.h"
#include "grid.h"

#define nxt(cpt) ((cpt+1)%2)

static Packet pckt;
static Client players[2];

//init both client & game main loop & replay loop
void run_game(Client, Client);

//wait replay Client request, kill_game if someone left
void replay(void);

//send to both Clients index of who start (random)
void send_ini(int);

//if Client quit then inform other & kill game
void send_client_quit(Client);

//send place token action to both Clients
void send_place_token(int,int);

//send winner index & win tokens to both Clients
void send_win(int,int*);

//send draw to both Clients
void send_draw(void);

//SIGINT signal handler & also called by send_client_quit
void kill_game(int);

#endif /* game_h */
