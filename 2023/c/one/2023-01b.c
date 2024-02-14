#include <stdio.h>
#include <stdlib.h>

const int A = (int)'a';
const int B = (int)'b';
const int C = (int)'c';
const int D = (int)'d';
const int E = (int)'e';
const int F = (int)'f';
const int G = (int)'g';
const int H = (int)'h';
const int I = (int)'i';
const int J = (int)'j';
const int K = (int)'k';
const int L = (int)'l';
const int M = (int)'m';
const int N = (int)'n';
const int O = (int)'o';
const int P = (int)'p';
const int Q = (int)'q';
const int R = (int)'r';
const int S = (int)'s';
const int T = (int)'t';
const int U = (int)'u';
const int V = (int)'v';
const int W = (int)'w';
const int X = (int)'x';
const int Y = (int)'y';
const int Z = (int)'z';

short int invalid_ch(int c)
{
    if (c <= 48 || (c >= 58 && c < A) || c > Z)
    {
        return 1;
    }
    return -1;
}

short int unused_ch(int c)
{
    if (c == A ||
        c == B ||
        c == C ||
        c == D ||
        c == J ||
        c == K ||
        c == L ||
        c == M ||
        c == P ||
        c == Q ||
        c == Y ||
        c == Z)
    {
        return 1;
    }
    return -1;
}

short int is_digit(int c)
{
    if (c > 48 && c < 58)
    {
        return 1;
    }
    return -1;
}

void assignChar(char *buffer, char ch)
{
    if (buffer[0] == '\0')
    {
        buffer[0] = ch;
    }
    buffer[1] = ch;
}

short int is_one(char *buffer)
{
    if (buffer[0] == O &&
        buffer[1] == N &&
        buffer[2] == E)
    {
        return 1;
    }
    return -1;
}

short int is_two(char *buffer)
{
    if (buffer[0] == T &&
        buffer[1] == W &&
        buffer[2] == O)
    {
        return 1;
    }
    return -1;
}

short int is_three(char *buffer)
{
    if (buffer[0] == T &&
        buffer[1] == H &&
        buffer[2] == R &&
        buffer[3] == E &&
        buffer[4] == E)
    {
        return 1;
    }
    return -1;
}

short int is_four(char *buffer)
{
    if (buffer[0] == F &&
        buffer[1] == O &&
        buffer[2] == U &&
        buffer[3] == R)
    {
        return 1;
    }
    return -1;
}

short int is_five(char *buffer)
{
    if (buffer[0] == F &&
        buffer[1] == I &&
        buffer[2] == V &&
        buffer[3] == E)
    {
        return 1;
    }
    return -1;
}

short int is_six(char *buffer)
{
    if (buffer[0] == S &&
        buffer[1] == I &&
        buffer[2] == X)
    {
        return 1;
    }
    return -1;
}

short int is_seven(char *buffer)
{
    if (buffer[0] == S &&
        buffer[1] == E &&
        buffer[2] == V &&
        buffer[3] == E &&
        buffer[4] == N)
    {
        return 1;
    }
    return -1;
}

short int is_eight(char *buffer)
{
    if (buffer[0] == E &&
        buffer[1] == I &&
        buffer[2] == G &&
        buffer[3] == H &&
        buffer[4] == T)
    {
        return 1;
    }
    return -1;
}

short int is_nine(char *buffer)
{
    if (buffer[0] == N &&
        buffer[1] == I &&
        buffer[2] == N &&
        buffer[3] == E)
    {
        return 1;
    }
    return -1;
}

char find_match(char *buffer)
{
    if (is_digit(buffer[0]) > 0)
    {
        return (char)buffer[0];
    }
    
    if (is_one(buffer) > 0)
    {
        return '1';
    }
    
    if (is_two(buffer) > 0)
    {
        return '2';
    }

    if (is_three(buffer) > 0)
    {
        return '3';
    }

    if (is_four(buffer) > 0)
    {
        return '4';
    }

    if (is_five(buffer) > 0)
    {
        return '5';
    }

    if (is_six(buffer) > 0)
    {
        return '6';
    }

    if (is_seven(buffer) > 0)
    {
        return '7';
    }

    if (is_eight(buffer) > 0)
    {
        return '8';
    }

    if (is_nine(buffer) > 0)
    {
        return '9';
    }

    return -1;
}

int main(int argc, char const *argv[])
{
    FILE *file = NULL;
    fopen_s(&file, "input.txt", "r");

    if (file == NULL)
    {
        return 1;
    }

    char buffer[255];
    char digits[] = {'\0', '\0', '\0'};
    int sum = 0;

    int c = 0;
    while (c != EOF)
    {
        for (short int i = 0; i < 255; i++)
        {
            buffer[i] = '\0';
        }

        for (short int i = 0; i < 255; i++)
        {
            c = fgetc(file);
            buffer[i] = c;
            if (c == 10 || c == EOF)
            {
                break;
            }
        }

        //printf("line = %s\n", buffer);

        for (short int i = 0; i < 255; i++)
        {
            c = buffer[i];

            if (c == 10 || c == EOF)
            {
                int num = atoi(digits);
                //printf("num=%i\n", num);
                sum += num;

                digits[0] = '\0';
                digits[1] = '\0';

                break;
            }

            if (invalid_ch(c) > 0 || unused_ch(c) > 0)
            {
                continue;
            }

            char match = find_match(&buffer[i]);

            if (match > 0)
            {
                //printf("matched char = %c\n", match);
                assignChar(digits, match);
            }
        }
    }

    fclose(file);

    printf("sum=%i\n", sum);

    return 0;
}
