//
//  game_server.h
//  p4rezServer
//
//  Created by Hugo on 09/11/2019.
//  Copyright © 2019 Hugo. All rights reserved.
//

#ifndef game_server_h
#define game_server_h

#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <stdarg.h>
#include <string.h>
#include <sys/wait.h>
#include <sys/select.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/poll.h>
#include <fcntl.h>
#include "struct.h"
#include "error.h"
#include "daemon.h"
#include "game.h"

#define PORT 8080
#define MAX_GAME 10
#define MAX_PENDING MAX_GAME*2

static pid_t dscvr_daemon_pid;
static int srvr_socket; //listening socket
static Game game_arr[MAX_GAME]; //array=[[pid_game, Client1, Client2],...]
static int nb_game;

//init_server() then wait for 2 Clients for add_bckgnd_game()
void run_server(void);

//init server environment & create_server_socket() & launch udp discover daemon
void init_server(void);

//init srvr_socket as a listening socket
void create_server_socket(void);

//accept & return one Client waiting in queue of pending connections
Client accept_new_client(void);

//store info from bytes received by Client into Packet, return nb bytes rcvd
size_t rcv(Client,Packet);

//send data contained in Packet to Client
size_t snd(Client, Packet);

//if no slot available on server return 0 else launch_game & return 1
int add_bckgnd_game(Client,Client);

//SIGINT signal handler
void kill_server(int);

//SIGCHLD signal handler
void chld_hdlr(int);

#endif /* game_server_h */
