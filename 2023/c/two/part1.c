#include <stdlib.h>
#include <stdio.h>
#include <string.h>

const short int MAX_RED = 12;
const short int MAX_GREEN = 13;
const short int MAX_BLUE = 14;

int main()
{
    FILE *file = NULL;
    fopen_s(&file, "input.txt", "r");

    if (file == NULL)
    {
        return 1;
    }

    short int sum = 0;
    char buffer[256];
    while (1 > 0)
    {
        memset(buffer, 0, sizeof(buffer));

        int c;
        for (short int i = 0; i < sizeof(buffer); i++)
        {
            c = fgetc(file);
            if (c == 10 || c == EOF)
            {
                break;
            }
            buffer[i] = (char)c;
        }

        if (buffer[0] == '\0')
        {
            break;
        }

        short int idx = 0;

        //drop 5
        idx += 5;

        char digits[] = {buffer[idx++], '\0', '\0', '\0'};
        char ch = buffer[idx++];
        if (ch != ':')
        {
            digits[1] = ch;

            ch = buffer[idx++];
            if (ch != ':')
            {
                digits[2] = ch;
            }
        }

        short int id = atoi(digits);
        digits[2] = '\0';

        //drop 1
        idx++;

        short int dice;
        for (idx; idx < sizeof(buffer); idx++)
        {
            //printf_s("remaining buffer: %s\n", &buffer[idx]);
            ch = buffer[idx++];
            if (ch == '\0')
            {
                //printf_s("adding id %i\n", id);
                sum += id;
                break;
            }

            digits[0] = ch;
            ch = buffer[idx++];
            if (ch != ' ')
            {
                //printf_s("second digit : %c\n", ch);
                digits[1] = ch;
                idx++;
            }
            else
            {
                digits[1] = '\0';
            }
            dice = atoi(digits);
            //printf_s("dice: %i\n", dice);
            // first letter of color
            ch = buffer[idx++];
            //printf_s("first letter of color: %c\n", ch);
            if (ch == 'r')
            {
                if (dice > MAX_RED)
                {
                    break;
                }
                idx+=3;
            }
            else if (ch == 'g')
            {
                if (dice > MAX_GREEN)
                {
                    break;
                }
                idx+=5;
            }
            else if (ch == 'b')
            {
                if (dice > MAX_BLUE)
                {
                    break;
                }
                idx+=4;
            }
        }
    }

    printf_s("sum = %i\n", sum);
    exit(EXIT_SUCCESS);
}
