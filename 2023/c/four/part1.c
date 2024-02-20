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

void collectNumbers(char *buffer, short *numbers, short size)
{
    char digits[3] = {'\0'};
    for (short i = 0; i < size; i++)
    {
        short pos = 3 * i;
        if (buffer[pos] == ' ')
        {
            digits[0] = '0';
        }
        else
        {
            digits[0] = buffer[pos];
        }
        digits[1] = buffer[pos + 1];

        numbers[i] = atoi(digits);
    }
}

#define N_WINNERS 10
#define N_NUMS 25

int main()
{
    FILE *file = NULL;
    fopen_s(&file, "input.txt", "r");

    if (file == NULL)
    {
        exit(EXIT_FAILURE);
    }

    long sum = 0;
    char buffer[120] = {'\0'};

    while (1 > 0)
    {
        readLine(file, buffer, sizeof(buffer));
        if (buffer[0] == '\0')
        {
            break;
        }

        short winners[N_WINNERS];
        collectNumbers(&buffer[10], winners, N_WINNERS);

        short numbers[N_NUMS];
        collectNumbers(&buffer[42], numbers, N_NUMS);

        short points = 0;
        for (short i = 0; i < N_NUMS; i++)
        {
            short x = numbers[i];
            for (short i = 0; i < N_WINNERS; i++)
            {
                if(winners[i] == x)
                {
                    if (points > 0)
                    {
                        points *= 2;
                    }
                    else
                    {
                        points = 1;
                    }
                    break;
                }
            }
        }
        sum += points;
    }

    printf_s("sum = %li\n", sum);
    exit(EXIT_SUCCESS);
}
