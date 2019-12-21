//
//  grid.c
//  p4rezServer
//
//  Created by Hugo on 11/11/2019.
//  Copyright © 2019 Hugo. All rights reserved.
//

#include "grid.h"

void init_grid(void){
    int l, c;
    
    for(l = 0; l < NB_LINE; l++){
        for(c = 0; c < NB_COL; c++)
            grid[l][c] = -1;
    }
}

int insert(int c, int player){
    int l;
    
    if(grid[0][c] != -1)
        return -1;
    
    for(l = NB_LINE - 1; l >= 0 && grid[l][c] != -1; l--);
    
    grid[l][c] = player;
    
    return l;
}

int *victory(int l, int c){
    int *tmp;
    
    if((tmp = check(l, c, 0, 1)) != NULL)
        return tmp;
    if((tmp = check(l, c, 1, 0)) != NULL)
        return tmp;
    if((tmp = check(l, c, 1, 1)) != NULL)
        return tmp;
    if((tmp = check(l, c, 1, -1)) != NULL)
        return tmp;
    
    return NULL;
}

int *check(int l, int c, int incrl, int incrc){
    int i, cpt, tmpl, tmpc, *tkn;
    cpt = 1;
    
    tkn = (int*)malloc(2*NB_TKN_WIN*sizeof(int));
    tkn[0] = l;
    tkn[1] = c;
    
    for(i = 1; i < NB_TKN_WIN; i++){
        tmpl = l + incrl * i;
        tmpc = c + incrc * i;
        if(cpt < NB_TKN_WIN && tmpl >= 0 && tmpc >= 0 && tmpl < NB_LINE && tmpc < NB_COL && grid[tmpl][tmpc] == grid[l][c]){
            tkn[cpt*2] = tmpl;
            tkn[cpt*2+1] = tmpc;
            cpt++;
        } else
            break;
    }
    
    if(cpt == NB_TKN_WIN)
        return tkn;
    
    for(i = 1; i < NB_TKN_WIN; i++){
        tmpl = l - incrl * i;
        tmpc = c - incrc * i;
        if(cpt < NB_TKN_WIN && tmpl >= 0 && tmpc >= 0 && tmpl < NB_LINE && tmpc < NB_COL && grid[tmpl][tmpc] == grid[l][c]){
            tkn[cpt*2] = tmpl;
            tkn[cpt*2+1] = tmpc;
            cpt++;
        } else
            break;
    }

    if(cpt == NB_TKN_WIN)
        return tkn;
    
    free(tkn);
    return NULL;
}
