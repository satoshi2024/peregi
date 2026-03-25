VSQL := 'INSERT INTO ' || VQR_TABLE_NAME || q'[
VALUES(
    :1,
    1,
    :2,
    :3,
    :4,
    :5,
    :6,
    :7,
    :8,
    :9,
    :10,
    :11,
    :12,
    :13,
    :14,
    :15,
    :16,
    :17,
    :18,
    0,
    :19,
    :20,
    :21,
    :22,
    :23,
    0,
    0,
    0,
    99991231,
    99991231,
    0,
    99991231,
    99991231,
    :24,
    :25,
    :26,
    :27,
    :28,
    :29,
    :30,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    1,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    NULL,
    0,
    NULL,
    NULL,
    NULL,
    :31,
    0,
    0,
    :32,
    0,
    0,
    :33,
    :34,
    :35,
    :36,
    0,
    :37,
    :38,
    :39
)]';

EXECUTE IMMEDIATE VSQL USING
    o_NKEY_RENBAN,           -- :1
    i_NOP_FLG,               -- :2
    i_NKAMOKU_CD,            -- :3
    i_NKAMOKUS_CD,           -- :4
    i_NKAMOKU_CD2,           -- :5
    i_NKAMOKUS_CD2,          -- :6
    i_NCHOTEINENDO,          -- :7
    i_NNENDOBUN,             -- :8
    i_NKIWARIDANTAI_CD,      -- :9
    i_NTSUCHI_NO,            -- :10
    i_NRONRIKIBETSU,         -- :11
    NRENBAN,                 -- :12
    i_NNOFUHOHO,             -- :13
    VNOUFU_NO,               -- :14
    o_VKAKUNIN_NO,           -- :15
    o_VNOUFU_KBN,            -- :16
    o_VSHIKIBETSU_NO,        -- :17
    i_VGYOMU_CD,             -- :18
    NSANSHOBI,               -- :19
    NKIBETSU,                -- :20
    NOKIGEN,                 -- :21
    i_NNOFUSHONOKIGEN,       -- :22
    NSHIHARAI_KIGEN,         -- :23
    NNOZEIGAKU,              -- :24
    NENTAIKIN,               -- :25
    NKA_SHO_SHINKOKU,        -- :26
    NFU_SHINKOKU_KASAN,      -- :27
    NJYU_KASAN,              -- :28
    KAKUSHUTESURYOGAKU,      -- :29
    NKONKAINOFUGAKU_SUM,     -- :30
    o_VQR,                   -- :31
    i_NSHORI_KBN,            -- :32
    i_NCHOHYOID,             -- :33
    i_NCHOHYOSHOSAIID,       -- :34
    i_NTOUROKU_RENBAN,       -- :35
    i_NSYS_SAKUSEIBI,        -- :36
    i_NSYS_JIKAN,            -- :37
    i_NSYS_SHOKUINKOJIN_NO,  -- :38
    i_VSYS_TANMATSU_NO;      -- :39