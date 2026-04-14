    } else {
        hyojiNayao = info.getCsvData();
    }
    
    // 追加检测并替换逻辑
    if (hyojiNayao != null && hyojiNayao.contains("★")) {
        hyojiNayao = hyojiNayao.replace("★", "｜");
    }
    
    row.setKomokuNayao(hyojiNayao);
    return row;
