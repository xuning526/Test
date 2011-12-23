;***************************************************************;
;*  Die Türme von Hanoi                           Lizenz: GPL  *;
;*                                                             *;
;*  (c) 2002  Roland Illig <1illig@informatik.uni-hamburg.de>  *;
;***************************************************************;

.model small, pascal

.stack 0400h

.code
start:
        mov     ax, @data
        mov     ds, ax
        mov     ax, 'a'
        push    ax
        inc     ax
        push    ax
        inc     ax
        push    ax
        mov     ax, 5
        push    ax
        call    bewege
        mov     ax, 4C00h
        int     21h

bewege proc
; Bewegt n Scheiben von Turm a nach Turm c und benutzt als Zwi-
; schenspeicher Turm b.
        arg a: Word, b: Word, c: Word, n: Word
        sub     n, 1
        jng     bewege_1
        push    a
        push    c
        push    b
        push    n
        call    bewege

bewege_1:
        mov     al, byte ptr a
        mov     msg_a, al
        mov     al, byte ptr c
        mov     msg_c, al

        mov     ah, 40h
        mov     bx, 1
        mov     cx, msg_len
        mov     dx, offset msg
        int     21h

        cmp     n, 0
        jng     bewege_2
        push    b
        push    a
        push    c
        push    n
        call    bewege

bewege_2:
      ret
bewege endp

.data
msg     db "Lege die oberste Scheibe von Turm "
msg_a   db "a"
        db " auf Turm "
msg_c   db "c"
        db ".", 13, 10
msg_len equ $-msg

end start
