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

//#define N_WINNERS 5
#define N_WINNERS 10
//#define N_NUMS 8
#define N_NUMS 25
//#define N_CARDS 6
#define N_CARDS 190

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
    long cards[N_CARDS];
    for (short i = 0; i < N_CARDS; i++)
    {
        cards[i] = 1;
    }

    while (1 > 0)
    {
        readLine(file, buffer, sizeof(buffer));
        if (buffer[0] == '\0')
        {
            break;
        }

        char digits[4];
        for (short i = 0; i < 3; i++)
        {
            digits[i] = buffer[5 + i];
        }
        short card = atoi(digits);

        short winners[N_WINNERS];
        //collectNumbers(&buffer[8], winners, N_WINNERS);
        collectNumbers(&buffer[10], winners, N_WINNERS);

        short numbers[N_NUMS];
        //collectNumbers(&buffer[25], numbers, N_NUMS);
        collectNumbers(&buffer[42], numbers, N_NUMS);

        short match = card;
        for (short i = 0; i < N_NUMS; i++)
        {
            short x = numbers[i];
            for (short i = 0; i < N_WINNERS; i++)
            {
                if(winners[i] == x)
                {
                    long num = cards[match];
                    cards[match++] = num + cards[card - 1];
                    break;
                }
            }
        }
    }

    for (short i = 0; i < N_CARDS; i++)
    {
        sum += cards[i];
    }

    printf_s("sum = %li\n", sum);
    exit(EXIT_SUCCESS);
}
