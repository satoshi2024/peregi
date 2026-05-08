            ELSE -- 令和2年分から令和6年分の場合
                -- 【修正开始】追加 所得税法別表第五 の丸め処理 (QA#22729 対応)
                IF NKYUYO_SHUNYU >= 1625000 AND NKYUYO_SHUNYU < 6600000 THEN
                    -- 給与等の収入金額が 1,625,000円～6,600,000円未満の場合
                    -- (収入金額 ÷ 4) の千円未満切捨て × 4 の処理
                    NNENCHO := TRUNC( NKYUYO_SHUNYU / 4000 ) * 4000;
                ELSE
                    -- 660万円以上、または162.5万円未満の場合は丸め処理なし
                    NNENCHO := NKYUYO_SHUNYU;
                END IF;
                -- 【修正结束】
                
                -- 使用经过别表第五处理后的 NNENCHO 去计算给与所得
                NRTN2 := FUNCSHOTOKU2021 ( NNENCHO, NKYUHOS );
                
                -- (如有其余针对 io_KYUYO 赋值等逻辑请保留原样)
