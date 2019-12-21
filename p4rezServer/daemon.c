//
//  daemon.c
//  p4rezServer
//
//  Created by Hugo on 09/11/2019.
//  Copyright © 2019 Hugo. All rights reserved.
//

#include "daemon.h"

void run_dscvr_daemon(){
    char buf[UDP_BUF_SZ];
    size_t n;
    struct sockaddr_in saddr, caddr;
    socklen_t ssz, csz;
    
    memset(buf, 0, UDP_BUF_SZ);
    gethostname(buf, UDP_BUF_SZ); //don't care if it fails
    
    //create new udp socket
    if((dscvr_socket = socket(AF_INET, SOCK_DGRAM, 0)) == ERR)
        syserror(1);
    
    //init client addr & server addr
    ssz = sizeof(saddr);
    csz = sizeof(caddr);
    
    memset(&saddr, 0, ssz);
    memset(&caddr, 0, csz);
    
    saddr.sin_family = AF_INET;
    saddr.sin_addr.s_addr = INADDR_ANY;
    saddr.sin_port = htons((uint16_t)PORT);
    
    //bind addr to socket to receive packets destined to any of the interfaces
    if(bind(dscvr_socket, (const struct sockaddr*)&saddr, ssz) == ERR)
        syserror(2);
    
    //daemon loop
    for(;;){
        //receive client datagram (blocking function)
        if((n = recvfrom(dscvr_socket, NULL, UDP_BUF_SZ, MSG_WAITALL, (struct sockaddr*)&caddr, &csz)) == ERR)
            syserror(3);
        
        //answer to Client to let him know server IP addr
        if(sendto(dscvr_socket, (const char *)buf, strlen(buf), MSG_SYN, (const struct sockaddr*)&caddr, csz) == ERR)
            syserror(4);
    }
}

void kill_dscvr_daemon(int sig){
    printf("kill daemon\n");
    
    if(close(dscvr_socket) == ERR)
        syserror(12);
    
    exit(0);
}
