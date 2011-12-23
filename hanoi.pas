program hanoi;

{
    Moves <n> disks from tower <a> to tower <c>, using tower <b>.
}
procedure Move(a, b, c: Char; n: Integer);
begin
  if n = 1 then
    WriteLn('Put the upper disk from tower ', a, ' to tower ', c, '.')
  else
    begin
      move(a, c, b, n-1);
      move(a, b, c, 1);
      move(b, a, c, n-1);
    end;
end;

{
    Prints out usage information.
}
procedure Usage;
begin
  WriteLn('usage: ', ParamStr(0), ' <n>');
  WriteLn(#9'where <n> is the number of disks');
  halt(1);
end;

var
  Disks: Integer;
  Error: Word;
begin
  if ParamCount <> 1 then
    Usage;

  Val(ParamStr(1), Disks, Error);
  if Error <> 0 then
    Usage;
  
  Move('a', 'b', 'c', Disks);
end.
