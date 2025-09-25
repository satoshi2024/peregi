CURSOR r IS
  SELECT *
  FROM abc t
 WHERE t.kojin_no = i_nkoujinnno
   AND t.nenbun   = i_nnendo
   AND t.renban = (
       SELECT MAX(t2.renban)
       FROM abc t2
       WHERE t2.kojin_no = t.kojin_no
         AND t2.nenbun   = t.nenbun
   );
