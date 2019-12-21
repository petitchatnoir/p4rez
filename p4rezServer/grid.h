//
//  grid.h
//  p4rezServer
//
//  Created by Hugo on 11/11/2019.
//  Copyright © 2019 Hugo. All rights reserved.
//

#ifndef grid_h
#define grid_h

#include <stdio.h>
#include <stdlib.h>

#define NB_LINE 6
#define NB_COL 7
#define NB_TKN_WIN 4

static int grid[NB_LINE][NB_COL];

void init_grid(void);

int insert(int,int);

int *victory(int,int);

int *check(int, int, int, int);

#endif /* grid_h */
