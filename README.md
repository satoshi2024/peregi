    FETCH CDATA INTO RDATA;
    EXIT WHEN CDATA%NOTFOUND;

    -- ====== 测试用：限制只处理特定的人员 ======
    IF RDATA.KOJIN_NO NOT IN ('1015340440', '1015335862') THEN
        CONTINUE;
    END IF;
    -- =======================================
