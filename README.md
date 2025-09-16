create or replace procedure lob_replace( p_lob in out clob,

p_what in varchar2,

p_with in varchar2 )

as

n number;

len number;

begin

n := dbms_lob.instr( p_lob, p_what );

while ( nvl(n,0) > 0 ) loop

len := dbms_lob.getlength(p_lob);

if (n+length(p_with)-1 > len)

then

dbms_lob.writeappend( p_lob, n+length(p_with)-1 - len, p_with );

end if;


if (len-n-length(p_what)+1 > 0)

then

dbms_lob.copy( p_lob,

p_lob,

len-n-length(p_what)+1,

n+length(p_with),

n+length(p_what) );

end if;


dbms_lob.write( p_lob, length(p_with), n, p_with );


if ( length(p_what) > length(p_with) )

then

dbms_lob.trim( p_lob,

dbms_lob.getlength(p_lob)-(length(p_what)-length(p_with)) );

end if;

n := dbms_lob.instr( p_lob, p_what );

end loop;

end;

