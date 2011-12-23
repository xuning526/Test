/*
    Beatnik interpreter
*/

/* standard C headers */
#ifndef S_SPLINT_S
#include <ctype.h>
#endif
#include <errno.h>
#include <inttypes.h>
#include <limits.h>
#include <stdarg.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define high(array) ((int) ((sizeof(array) / sizeof(array[0])) - 1))

/*@observer@*/ static const char program_name[] = "bni";
/*@observer@*/ static const char program_version[] = "1.0.0";
/*@observer@*/ static const char program_copyright[] = "(c) 2003 Roland Illig";

static int scrabble_table[(unsigned int) UCHAR_MAX + 1U];

/*@observer@*/ static const char* opcode[] =
    {"put", "ignor", "addit", "igetc", "oputc", "subsub",
    "swapit", "dupdup", "bz", "bnz", "bzre", "bnzre", "terminatenow"};

/*
 * The interpreter relies on these facts:
 * - OPCODE_PUSH has the minimum value.
 * - the opcodes are numbered sequentially.
 * - the opcodes for SAZ, SANZ, SBZ, and SBNZ are continuous
 *   in exactly this order.
 * - OPCODE_HALT has the maximum value.
 */
#define OPCODE_PUSH ((uint8_t) 5U)
#define OPCODE_POP  ((uint8_t) 6U)
#define OPCODE_ADD  ((uint8_t) 7U)
#define OPCODE_GETC ((uint8_t) 8U)
#define OPCODE_PUTC ((uint8_t) 9U)
#define OPCODE_SUB  ((uint8_t) 10U)
#define OPCODE_SWAP ((uint8_t) 11U)
#define OPCODE_DUP  ((uint8_t) 12U)
#define OPCODE_SAZ  ((uint8_t) 13U) /* skip ahead if zero */
#define OPCODE_SANZ ((uint8_t) 14U) /* skip ahead if not zero */
#define OPCODE_SBZ  ((uint8_t) 15U) /* skip back if zero */
#define OPCODE_SBNZ ((uint8_t) 16U) /* skip back if not zero */
#define OPCODE_HALT ((uint8_t) 17U)

#define IS_VALID_OPCODE(opc) (((opc) >= OPCODE_PUSH && (opc) <= OPCODE_HALT))

static uint8_t text[10000];
static uint8_t data[10000];

static size_t text_len;
static int text_ptr, data_ptr;

static bool debug_flag = false;



static void init_scrabble_table(void)
/*@globals scrabble_table; @*/
/*@modifies scrabble_table[]; @*/
{
    const unsigned char ALPHABET_LETTERS[26] = { 'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    const int SCRABBLE_VALUE[26] = {1,3,3,2,1,4,2,4,1,8,5,1,3,1,1,3,10,1,1,1,1,4,4,8,4,10};
    size_t i;

    for (i = 0; i <= (size_t) UCHAR_MAX; i++) {
        scrabble_table[i] = 0;
    }
    for (i = 0; i < sizeof(ALPHABET_LETTERS); i++) {
        scrabble_table[(size_t) ALPHABET_LETTERS[i]] = SCRABBLE_VALUE[i];
    }
}

static int load_program(const char *fname)
/*@globals fileSystem,
    errno, stderr,
    program_name,
    text_len, text, scrabble_table; @*/
/*@modifies fileSystem,
    errno, *stderr,
    text_len, text[]; @*/
{
    unsigned int score = 0;
    int c = 0;
    FILE *f;
    

    f = fopen(fname, "rb");
    if (f == NULL) {
	return -1;
    }

    text_len = 0;
    while ((c = fgetc(f)) != EOF) {
        c = (int)toupper(c);
	if (isalpha(c)) {
	    score += scrabble_table[(size_t) c];
	} else {
	    if (score != 0) {
		if (text_len < sizeof(text)) {
		    text[text_len] = (uint8_t) score;
		    if ((unsigned int) text[text_len] != score) {
		        (void)fprintf(stderr, "%s: integer overflow while parsing the code\n", program_name);
			exit(EXIT_FAILURE);
		    }
		    text_len = text_len + 1;
		} else {
		    (void)fclose(f);
		    errno = EFBIG;
		    return -1;
		}
		score = 0;
	    }
	}
    }
    
    if (text_len < sizeof(text)) {
        text[text_len++] = (uint8_t) 17U; /* stop the program */
    } else {
	(void)fclose(f);
	errno = EFBIG;
	return -1;
    }
    (void)fclose(f);
    return 0;
}

static int run(void)
/*@globals fileSystem,
    program_name,
    text, text_ptr, data, data_ptr, debug_flag, opcode,
    stderr, errno, stdout, stdin; @*/
/*@modifies fileSystem,
    text_ptr, data[], data_ptr,
    *stderr, errno, *stdout, *stdin; @*/
{
    text_ptr = 0;
    data_ptr = -1;
    while (true) {
	if (debug_flag) {
	    int i;
	    (void)fprintf(stderr, "(");
	    for (i = 0; i <= data_ptr; i++) {
		(void)fprintf(stderr, "%u%s", (unsigned int) data[i], (i != data_ptr) ? " " : "");
	    }
	    (void)fprintf(stderr, ")\n");
	    
	    if (text[text_ptr] == OPCODE_PUSH || (text[text_ptr] >= OPCODE_SAZ && text[text_ptr] <= OPCODE_SBNZ)) {
		(void)fprintf(stderr, "%04d: %s %u\n", text_ptr, opcode[text[text_ptr]-5], (unsigned int) text[text_ptr+1]);
	    } else if (IS_VALID_OPCODE(text[text_ptr])) {
		(void)fprintf(stderr, "%04d: %s\n", text_ptr, opcode[text[text_ptr]-5]);
	    } else {
		(void)fprintf(stderr, "%04d: (UNDEFINED BEHAVIOUR: %u)\n", text_ptr, (unsigned int) text[text_ptr]);
	    }
	    if (fflush(stderr) == EOF) {
	        perror(program_name);
		exit(EXIT_FAILURE);
	    }
	}
	switch (text[text_ptr]) {
	
	    case 5: /* push */
		if (data_ptr + 1 <= high(data)) {
		    data[++data_ptr] = text[++text_ptr];
		} else {
		    return -1;
		}
		/*@switchbreak@*/ break;

	    case 6: /* pop */
		if (data_ptr >= 0) {
		    data_ptr--;
		} else {
		    return -1;
		}
		/*@switchbreak@*/ break;
	
	    case 7: /* add */
		if (data_ptr - 1 >= 0) {
		    data[data_ptr - 1] += data[data_ptr];
		    data_ptr--;
		} else {
		    return -1;
		}
		/*@switchbreak@*/ break;
	
	    case 8: /* getc */
		if (data_ptr + 1 <= high(data)) {
		    int c;
		    
		    c = getchar();
		    if (c == EOF) {
		        /* let EOF be equivalent to a NUL character */
		        c = 0;
		    }
		    data[++data_ptr] = (uint8_t) (unsigned char) c;
		} else {
		    return -1;
		}
		/*@switchbreak@*/ break;
		
	    case 9: /* putc */
		if (data_ptr >= 0) {
		    if (putchar((int) data[data_ptr--]) == EOF) {
		    	perror(program_name);
			exit(EXIT_FAILURE);
		    }
		    if (fflush(stdout) == EOF) {
		        perror(program_name);
			exit(EXIT_FAILURE);
		    }
		} else {
		    return -1;
		}
		/*@switchbreak@*/ break;
		
	    case 10: /* sub */
		if (data_ptr - 1 >= 0) {
		    data[data_ptr - 1] -= data[data_ptr];
		    data_ptr--;
		} else {
		    return -1;
		}
		/*@switchbreak@*/ break;
	
	    case 11: /* swap */
		if (data_ptr - 1 >= 0) {
		    uint8_t tmp = data[data_ptr - 1];
		    data[data_ptr - 1] = data[data_ptr];
		    data[data_ptr] = tmp;
		} else {
		    return -1;
		}
		/*@switchbreak@*/ break;

	    case 12: /* dup */
		if (data_ptr + 1 <= high(data)) {
		    data[data_ptr + 1] = data[data_ptr];
		    ++data_ptr;
		} else {
		    return -1;
		}
		/*@switchbreak@*/ break;
		
	    case 13: /* jz_forward */
		if (data_ptr <= high(data)) {
		    text_ptr++;
		    if (data[data_ptr--] == 0) {
			text_ptr += text[text_ptr];
		    }
		} else {
		    return -1;
		}
		/*@switchbreak@*/ break;
		
	    case 14: /* jnz_forward */
		if (data_ptr <= high(data)) {
		    text_ptr++;
		    if (data[data_ptr--] != 0) {
			text_ptr += text[text_ptr];
		    }
		} else {
		    return -1;
		}
		/*@switchbreak@*/ break;

	    case 15: /* jz_backward */
		if (data_ptr <= high(data)) {
		    text_ptr++;
		    if (data[data_ptr--] == 0) {
			text_ptr -= text[text_ptr];
		    }
		} else {
		    return -1;
		}
		/*@switchbreak@*/ break;

	    case 16: /* jnz_backward */
		if (data_ptr <= high(data)) {
		    text_ptr++;
		    if (data[data_ptr--] != 0) {
			text_ptr -= text[text_ptr];
		    }
		} else {
		    return -1;
		}
		/*@switchbreak@*/ break;
		
	    case 17: /* hlt */
		return 0;
	
	    default:
		; /* be a nice interpreter */
	}
	text_ptr++;	    
    }
}

/*@shared@*/ static const char *itobna(uint8_t i) /*@*/
{
    static char bna[256];
    char *p = bna;
    while(i >= (uint8_t) 8U) {
	*p++ = 'x';
	i -= 8;
    }
    while (i >= (uint8_t) 4U) {
	*p++ = 'v';
	i -= 4;
    }
    while (i >= (uint8_t) 1U) {
	*p++ = 'i';
	i -= 1;
    }
    *p = '\0';
    return bna;
}

static void printf_checked(const char *fmt, ...)
/*@globals fileSystem,
    program_name, stdout, errno, stderr; @*/
/*@modifies fileSystem, *stdout, errno, *stderr; @*/
{
    va_list args;
    
    va_start(args, fmt);
    if (vfprintf(stdout, fmt, args) < 0) {
        perror(program_name);
	exit(EXIT_FAILURE);
    }
    va_end(args);
}

static int disassemble(void)
/*@globals fileSystem,
    program_name,
    text_len, text, opcode,
    stdout, stderr, errno; @*/
/*@modifies fileSystem,
    *stdout, *stderr, errno; @*/
{
    size_t i;
    for (i = 0; i < text_len; i++) {
        if (text[i] == OPCODE_PUSH) {
	    printf_checked("%04u: %s %s\n", (unsigned int) i, opcode[text[i]-5], itobna(text[i+1]));
	    i++;
	} else if (text[i] >= OPCODE_SAZ && text[i] <= OPCODE_SBNZ) {
	    printf_checked("%04u: %s %s (= %04d)\n", (unsigned int) i, opcode[text[i]-5], 
		itobna(text[i+1]), i + 2 + ((text[i] >= OPCODE_SBZ) ? -text[i+1] : text[i+1]));
	    i++;
	} else if (IS_VALID_OPCODE(text[i])) {
	    printf_checked("%04u: %s\n", (unsigned int) i, opcode[text[i]-5]);
	} else {
	    printf_checked("%04u: (UNDEFINED BEHAVIOUR: %s)\n", (unsigned int) i, itobna(text[i]));
	}
    }
    return 0;
}

/*@noreturn@*/ static void usage(void)
/*@globals fileSystem,
    stderr,
    program_name, program_version, program_copyright; @*/
/*@modifies fileSystem,
    *stderr; @*/
{
    (void)fprintf(stderr, "%s %s  %s\n", program_name, program_version,
        program_copyright);
    (void)fprintf(stderr, "usage: bni [--debug | --disasm] <filename>\n");
    exit(EXIT_FAILURE);
}

int main(int argc, char **argv)
/*@globals fileSystem,
    program_name, program_version, program_copyright,
    scrabble_table, text_len, text, data, text_ptr, data_ptr, opcode,
    debug_flag,
    stderr, errno, stdin, stdout; @*/
/*@modifies fileSystem,
    scrabble_table, text_len, text[], data[], text_ptr, data_ptr,
    debug_flag, *stderr, errno, *stdin, *stdout; @*/
{
    int i;
    const char *fname;
    bool disassemble_flag;

    init_scrabble_table();
    
    fname = NULL;
    disassemble_flag = false;
    for (i = 1; i < argc; i++) {
	if (strcmp(argv[i], "--debug") == 0) {
	    debug_flag = true;
	} else if (strcmp(argv[i], "--disasm") == 0) {
	    disassemble_flag = true;
	} else if (fname == NULL) {
	    fname = argv[i];
	} else {
	    usage();
	}
    }
    if (fname == NULL) {
	usage();
    }

    if (load_program(fname) == -1) {
	perror("bni");
	exit(EXIT_FAILURE);
    }
    if (disassemble_flag) {
	if (disassemble() == -1) {
	    (void)fprintf(stderr, "error disassembling the program\n");
	    exit(EXIT_FAILURE);
	}
    } else {
	if (run() == -1) {
	    (void)fprintf(stderr, "error running the program\n");
	    exit(EXIT_FAILURE);
	}
    }
    exit(EXIT_SUCCESS);
}
