//
//  error.h
//  mysh
//
//  Created by Hugo on 26/10/2019.
//  Copyright © 2019 Hugo. All rights reserved.
//

#ifndef error_h
#define error_h

#include <stdio.h>

#define ERR -1
#define locate_err printf ("line %d of file \"%s\" (function <%s>)\n",\
__LINE__, __FILE__, __func__)
#define syserror(n)({locate_err;perror(errsys[n]);fflush(stderr);exit(n);})
#define print_status(status){if(WIFEXITED(status)){printf("status code = %d\n", WEXITSTATUS(status));}else{printf("unknown status code\n");}}

static char *errsys[] = {"No error", "Socket", "Bind", "Recv", "Send", "Fork", "Kill", "Pause", "Signal", "Setsockopt", "Listen", "Accept", "Close", "Write", "Read", "Fcntl", "Poll"};

#endif /* error_h */
