#ifndef CONTROLLER_H__
#define CONTROLLER_H__

#include <ncurses.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>

#include "characters_movement.h"
#include "consumables_actions.h"
#include "data_actions.h"
#include "entities.h"
#include "entities_consts.h"
#include "generation.h"
#include "presentation.h"
#include "screen.h"
#include "statistics.h"

#define ESCAPE ((char)27)

void game_cycle(player_t* player, level_t* level, map_t* map,
                battle_info_t* battles, const char* save, const char* score,
                const char* stat);

#endif  // CONTROLLER_H__
