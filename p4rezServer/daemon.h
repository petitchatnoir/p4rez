//
//  daemon.h
//  p4rezServer
//
//  Created by Hugo on 09/11/2019.
//  Copyright © 2019 Hugo. All rights reserved.
//

#ifndef daemon_h
#define daemon_h

#include <signal.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "error.h"

#define PORT 8080
#define UDP_BUF_SZ 32

static int dscvr_socket;

//daemon loop, receive/answer IP addr request
void run_dscvr_daemon(void);

//SIGINT signal handler
void kill_dscvr_daemon(int);

#endif /* daemon_h */
