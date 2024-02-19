#include <stdlib.h>
#include <stdio.h>
#include <string.h>

void readLine(FILE *file, char *buffer, short size)
{
    memset(buffer, 0, size);

    int c;
    for (short i = 0; i < size; i++)
    {
        c = fgetc(file);
        if (c == 10 || c == EOF)
        {
            break;
        }
        buffer[i] = (char)c;
    }
}

const short GRID_SIZE = 140;

struct coord
{
    short x;
    short y;
};

int slurp_right(char (*grid)[140], struct coord c)
{
    if (c.x < GRID_SIZE && grid[c.y][c.x + 1] != '\0')
    {
        short idx = 0;
        char buffer[141] = {'\0'};
        for (short i = c.x + 1; i < GRID_SIZE; i++)
        {
            char ch = grid[c.y][i];
            if (ch == '\0')
            {
                break;
            }
            buffer[idx++] = ch;
            // clear numbers so they dont get counted twice
            grid[c.y][i] = '\0';
        }

        int num = atoi(buffer);
        return num;
    }
}

int slurp_left(char (*grid)[140], struct coord c)
{
    if (c.x > 0 && grid[c.y][c.x - 1] > '\0')
    {
        for (short i = c.x - 1; i >= -1; i--)
        {
            if (i == -1 || '\0' == grid[c.y][i])
            {
                struct coord c2 = {i, c.y};
                return slurp_right(grid, c2);
            }
        }
    }
    return 0;
}

int * slurp_up(char (*grid)[140], struct coord c)
{
    static int result[2];
    result [0] = 0;
    result [1] = 0;
    if (c.y > 0)
    {
        if (grid[c.y - 1][c.x] > '\0')
        {
            for (short i = c.x - 1; i >= -1; i--)
            {
                if (i == -1 || grid[c.y - 1][i] == '\0')
                {
                    struct coord c2 = {i, c.y - 1};
                    result[0] = slurp_right(grid, c2);
                    return result;
                }
            }
        }
        else
        {
            struct coord c2 = {c.x, c.y - 1};
            result[0] = slurp_left(grid, c2);
            result[1] = slurp_right(grid, c2);
            return result;
        } 
    }
    return result;
}

int * slurp_down(char (*grid)[140], struct coord c)
{
    static int result[2];
    result [0] = 0;
    result [1] = 0;
    if (c.y < GRID_SIZE - 1)
    {
        if (grid[c.y + 1][c.x] > '\0')
        {
            for (short i = c.x - 1; i >= -1; i--)
            {
                if (i == -1 || grid[c.y + 1][i] == '\0')
                {
                    struct coord c2 = {i, c.y + 1};
                    result[0] = slurp_right(grid, c2);
                    return result;
                }
            }
        }
        else
        {
            struct coord c2 = {c.x, c.y + 1};
            result[0] = slurp_left(grid, c2);
            result[1] = slurp_right(grid, c2);
            return result;
        } 
    }
    return result;
}

int main()
{
    FILE *file = NULL;
    fopen_s(&file, "input.txt", "r");

    if (file == NULL)
    {
        exit(EXIT_FAILURE);
    }

    long sum = 0;
    char buffer[141];
    char grid[140][140];

    short idx = 0;
    struct coord coords[140 * 140];
    for (short y = 0; y < GRID_SIZE; y++)
    {
        readLine(file, buffer, sizeof(buffer));
        if (buffer[0] == '\0')
        {
            break;
        }
        
        for (short x = 0; x < GRID_SIZE; x++)
        {
            char ch = buffer[x];
            if (ch >= '0' && ch <= '9')
            {
                grid[y][x] = ch;
                continue;
            }

            if (ch == '*')
            {
                struct coord xy = {x, y};
                coords[idx++] = xy;
            }

            grid[y][x] = 0;
        }
    }

    for (short i = 0; i < idx; i++)
    {
        struct coord c = coords[i];

        int left = slurp_left(grid, c);
        int right = slurp_right(grid, c);
        int *up = slurp_up(grid, c);
        int *down = slurp_down(grid, c);

        if (up[0] > 0 && up[1] > 0)
        {
            if (left > 0 || right > 0 || down[0] > 0 || down[1] > 1)
            {
                continue;
            }
            sum += up[0] * up[1];
            continue;
        }

        if (down[0] > 0 && down[1] > 0)
        {
            if (left > 0 || right > 0 || up[0] > 0 || up[1] > 0)
            {
                continue;
            }
            sum += down[0] * down[1];
            continue;
        }

        if (up[0] == 0 && up[1] > 0)
        {
            up[0] = up[1];
        }

        if (down[0] == 0 && down[1] > 0)
        {
            down[0] = down[1];
        }

        if (left > 0)
        {
            if (right > 0)
            {
                if (up[0] > 0 || down[0] > 0)
                {
                    continue;
                }
                sum += left * right;
                continue;
            }

            if (up[0] > 0)
            {
                if (down[0] > 0)
                {
                    continue;
                }
                sum += left * up[0];
                continue;
            }

            if (down[0] > 0)
            {
                sum += left * down[0];
            }

            continue;
        }

        if (right > 0)
        {
            if (up[0] > 0)
            {
                if (down[0] > 0)
                {
                    continue;
                }
                sum += right * up[0];
                continue;
            }
            
            if (down[0] > 0)
            {
                sum += right * down[0];
                continue;
            }
        }

        if (up[0] > 0 && down[0] > 0)
        {
            sum += up[0] * down[0];
            continue;
        }
    }

    printf_s("sum = %li\n", sum);
    exit(EXIT_SUCCESS);
}
