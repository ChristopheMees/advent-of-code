#include <stdio.h>
#include <stdlib.h>

int main(int argc, char const *argv[])
{
    FILE *file;
    fopen_s(&file, "input.txt", "r");

    if (file == NULL)
    {
        return 1;
    }

    int c;
    char digits[3];
    int sum;
    while (1 == 1)
    {
        c = fgetc(file);

        if (c == 10 || c == EOF)
        {
            int num = atoi(digits);
            printf("num=%i\n", num);
            sum += num;

            digits[0] = '\0';
            digits[1] = '\0';
        }

        if (c == EOF)
        {
            break;
        }

        if (c > 48 && c < 58)
        {
            char ch = (char)c;
            if (digits[0] == '\0')
            {
                digits[0] = ch;
            }
            digits[1] = ch;
        }
    }

    fclose(file);

    printf("sum=%i\n", sum);

    return 0;
}
