-- ============================================================
-- schema_mysql.sql – Normalized MySQL version
-- Database: purchase_db
-- Charset:  utf8mb4 (hỗ trợ đầy đủ Unicode + emoji)
-- ============================================================

CREATE DATABASE IF NOT EXISTS purchase_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE purchase_db;

-- 1. Xóa các bảng cũ đi để tạo lại cho sạch từ đầu
DROP TABLE IF EXISTS url_m;
DROP TABLE IF EXISTS toiawase_m;
DROP TABLE IF EXISTS todofuken_m;
DROP TABLE IF EXISTS rgon_m;

-- ============================================================
-- Table: rgon_m (地域マスタ – Region Master)
-- ============================================================
CREATE TABLE rgon_m (
    rgon_cd      INT          NOT NULL COMMENT '地域コード (Region code)',
    rgon_nm      VARCHAR(100) NOT NULL COMMENT '地域名称 (Region name)',
    rgon_use_typ CHAR(1)      NOT NULL DEFAULT '1' COMMENT '使用区分 1=有効',
    disp_order   INT          NOT NULL DEFAULT 0 COMMENT '表示順',
    delete_flg   CHAR(1)      NOT NULL DEFAULT '0' COMMENT '削除フラグ 0=有効 1=削除',
    PRIMARY KEY (rgon_cd)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地域マスタ';

-- ============================================================
-- Table: todofuken_m (都道府県マスタ – Prefecture Master)
-- ============================================================
CREATE TABLE todofuken_m (
    pref_cd    INT          NOT NULL COMMENT '都道府県コード',
    pref_nm    VARCHAR(20)  NOT NULL COMMENT '都道府県名',
    rgon_cd    INT          NOT NULL COMMENT '地域コード (FK → rgon_m)',
    disp_order INT          NOT NULL DEFAULT 0,
    delete_flg CHAR(1)      NOT NULL DEFAULT '0',
    PRIMARY KEY (pref_cd),
    CONSTRAINT fk_pref_rgon FOREIGN KEY (rgon_cd) REFERENCES rgon_m(rgon_cd)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='都道府県マスタ';

-- ============================================================
-- Table: toiawase_m (問合せ先マスタ – Contact Master)
-- ============================================================
CREATE TABLE toiawase_m (
    ref_cd     VARCHAR(10)  NOT NULL COMMENT '問合せ先コード',
    ref_nm     VARCHAR(200) NOT NULL COMMENT '問合せ先名称',
    ref_kn     VARCHAR(200)          COMMENT '問合せ先名称カナ',
    tel_no     VARCHAR(20)           COMMENT '電話番号',
    pref_cd    INT          NOT NULL COMMENT '都道府県コード (FK → todofuken_m)',
    delete_flg CHAR(1)      NOT NULL DEFAULT '0',
    PRIMARY KEY (ref_cd),
    CONSTRAINT fk_ref_pref FOREIGN KEY (pref_cd) REFERENCES todofuken_m(pref_cd)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='問合せ先マスタ';

-- Tối ưu hóa API tìm kiếm bằng Index kết hợp cờ hiệu xóa và mã tỉnh trực tiếp
CREATE INDEX idx_toiawase_m_search ON toiawase_m (delete_flg, pref_cd);

-- ============================================================
-- Table: url_m (URLマスタ – URL Master)
-- ============================================================
CREATE TABLE url_m (
    ref_cd     VARCHAR(10)  NOT NULL COMMENT '問合せ先コード (FK → toiawase_m)',
    disp_order INT          NOT NULL COMMENT '表示順',
    url_adr    VARCHAR(500) NOT NULL COMMENT 'URLアドレス',
    delete_flg CHAR(1)      NOT NULL DEFAULT '0',
    PRIMARY KEY (ref_cd, disp_order),    -- Khóa chính kép (Composite Key) chuẩn hóa
    CONSTRAINT fk_url_ref FOREIGN KEY (ref_cd) REFERENCES toiawase_m(ref_cd)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='URLマスタ';


-- ============================================================
-- Seed Data (Sample)
-- ============================================================

INSERT INTO rgon_m (rgon_cd, rgon_nm, rgon_use_typ, disp_order) VALUES
(1, '北海道・東北', '1', 1),
(2, '関東',         '1', 2),
(3, '中部',         '1', 3),
(4, '近畿',         '1', 4),
(5, '中国・四国',   '1', 5),
(6, '九州・沖縄',   '1', 6),
(7, '海外',         '1', 7);

INSERT INTO todofuken_m (pref_cd, pref_nm, rgon_cd, disp_order) VALUES
(1,  '北海道', 1, 1),
(2,  '青森県', 1, 2),
(13, '東京都', 2, 1),
(14, '神奈川県', 2, 2),
(11, '埼玉県', 2, 3),
(27, '大阪府', 4, 1),
(28, '兵庫県', 4, 2),
(40, '福岡県', 6, 1),
(99, '海外 (Overseas)', 7, 1);       -- Tạo thị trường giả cho Hải ngoại

INSERT INTO toiawase_m (ref_cd, ref_nm, ref_kn, tel_no, pref_cd) VALUES
('REF001', '東京チケットセンター',   'トウキョウチケットセンター',   '03-1234-5678', 13),
('REF002', '大阪公演案内所',         'オオサカコウエンアンナイジョ', '06-2345-6789', 27),
('REF003', '福岡エンタメ窓口',       'フクオカエンタメマドグチ',     '092-345-6789', 40),
('REF004', '北海道イベント事務局',   'ホッカイドウイベントジムキョク','011-234-5678', 1),
('REF005', '神奈川チケットサービス', 'カナガワチケットサービス',     '045-345-6789', 14),
('REF006', 'Global Ticket Support', 'グローバル・サポート',       '+1-800-123-456', 99); -- Liên hệ ngoài nước

INSERT INTO url_m (ref_cd, disp_order, url_adr) VALUES
('REF001', 1, 'https://tokyo.ticket.example.com'),
('REF001', 2, 'https://tokyo.ticket.example.com/en'),
('REF002', 1, 'https://osaka.ticket.example.com'),
('REF003', 1, 'https://fukuoka.ticket.example.com'),
('REF004', 1, 'https://hokkaido.ticket.example.com'),
('REF005', 1, 'https://kanagawa.ticket.example.com'),
('REF006', 1, 'https://global.example.com');
INSERT INTO toiawase_m (ref_cd, ref_nm, ref_kn, tel_no, pref_cd) VALUES
('REF100', 'Test Contact 100', 'テストレンラクサキ 100', '03-0000-0100', 13),
('REF101', 'Test Contact 101', 'テストレンラクサキ 101', '03-0000-0101', 13),
('REF102', 'Test Contact 102', 'テストレンラクサキ 102', '03-0000-0102', 13),
('REF103', 'Test Contact 103', 'テストレンラクサキ 103', '03-0000-0103', 13),
('REF104', 'Test Contact 104', 'テストレンラクサキ 104', '03-0000-0104', 13),
('REF105', 'Test Contact 105', 'テストレンラクサキ 105', '03-0000-0105', 13),
('REF106', 'Test Contact 106', 'テストレンラクサキ 106', '03-0000-0106', 13),
('REF107', 'Test Contact 107', 'テストレンラクサキ 107', '03-0000-0107', 13),
('REF108', 'Test Contact 108', 'テストレンラクサキ 108', '03-0000-0108', 13),
('REF109', 'Test Contact 109', 'テストレンラクサキ 109', '03-0000-0109', 13),
('REF110', 'Test Contact 110', 'テストレンラクサキ 110', '03-0000-0110', 13),
('REF111', 'Test Contact 111', 'テストレンラクサキ 111', '03-0000-0111', 13),
('REF112', 'Test Contact 112', 'テストレンラクサキ 112', '03-0000-0112', 13),
('REF113', 'Test Contact 113', 'テストレンラクサキ 113', '03-0000-0113', 13),
('REF114', 'Test Contact 114', 'テストレンラクサキ 114', '03-0000-0114', 13),
('REF115', 'Test Contact 115', 'テストレンラクサキ 115', '03-0000-0115', 13),
('REF116', 'Test Contact 116', 'テストレンラクサキ 116', '03-0000-0116', 13),
('REF117', 'Test Contact 117', 'テストレンラクサキ 117', '03-0000-0117', 13),
('REF118', 'Test Contact 118', 'テストレンラクサキ 118', '03-0000-0118', 13),
('REF119', 'Test Contact 119', 'テストレンラクサキ 119', '03-0000-0119', 13),
('REF120', 'Test Contact 120', 'テストレンラクサキ 120', '03-0000-0120', 13),
('REF121', 'Test Contact 121', 'テストレンラクサキ 121', '03-0000-0121', 13),
('REF122', 'Test Contact 122', 'テストレンラクサキ 122', '03-0000-0122', 13),
('REF123', 'Test Contact 123', 'テストレンラクサキ 123', '03-0000-0123', 13),
('REF124', 'Test Contact 124', 'テストレンラクサキ 124', '03-0000-0124', 13),
('REF125', 'Test Contact 125', 'テストレンラクサキ 125', '03-0000-0125', 13),
('REF126', 'Test Contact 126', 'テストレンラクサキ 126', '03-0000-0126', 13),
('REF127', 'Test Contact 127', 'テストレンラクサキ 127', '03-0000-0127', 13),
('REF128', 'Test Contact 128', 'テストレンラクサキ 128', '03-0000-0128', 13),
('REF129', 'Test Contact 129', 'テストレンラクサキ 129', '03-0000-0129', 13),
('REF130', 'Test Contact 130', 'テストレンラクサキ 130', '03-0000-0130', 13),
('REF131', 'Test Contact 131', 'テストレンラクサキ 131', '03-0000-0131', 13),
('REF132', 'Test Contact 132', 'テストレンラクサキ 132', '03-0000-0132', 13),
('REF133', 'Test Contact 133', 'テストレンラクサキ 133', '03-0000-0133', 13),
('REF134', 'Test Contact 134', 'テストレンラクサキ 134', '03-0000-0134', 13),
('REF135', 'Test Contact 135', 'テストレンラクサキ 135', '03-0000-0135', 13),
('REF136', 'Test Contact 136', 'テストレンラクサキ 136', '03-0000-0136', 13),
('REF137', 'Test Contact 137', 'テストレンラクサキ 137', '03-0000-0137', 13),
('REF138', 'Test Contact 138', 'テストレンラクサキ 138', '03-0000-0138', 13),
('REF139', 'Test Contact 139', 'テストレンラクサキ 139', '03-0000-0139', 13),
('REF140', 'Test Contact 140', 'テストレンラクサキ 140', '03-0000-0140', 13),
('REF141', 'Test Contact 141', 'テストレンラクサキ 141', '03-0000-0141', 13),
('REF142', 'Test Contact 142', 'テストレンラクサキ 142', '03-0000-0142', 13),
('REF143', 'Test Contact 143', 'テストレンラクサキ 143', '03-0000-0143', 13),
('REF144', 'Test Contact 144', 'テストレンラクサキ 144', '03-0000-0144', 13),
('REF145', 'Test Contact 145', 'テストレンラクサキ 145', '03-0000-0145', 13),
('REF146', 'Test Contact 146', 'テストレンラクサキ 146', '03-0000-0146', 13),
('REF147', 'Test Contact 147', 'テストレンラクサキ 147', '03-0000-0147', 13),
('REF148', 'Test Contact 148', 'テストレンラクサキ 148', '03-0000-0148', 13),
('REF149', 'Test Contact 149', 'テストレンラクサキ 149', '03-0000-0149', 13),
('REF150', 'Test Contact 150', 'テストレンラクサキ 150', '03-0000-0150', 13),
('REF151', 'Test Contact 151', 'テストレンラクサキ 151', '03-0000-0151', 13),
('REF152', 'Test Contact 152', 'テストレンラクサキ 152', '03-0000-0152', 13),
('REF153', 'Test Contact 153', 'テストレンラクサキ 153', '03-0000-0153', 13),
('REF154', 'Test Contact 154', 'テストレンラクサキ 154', '03-0000-0154', 13),
('REF155', 'Test Contact 155', 'テストレンラクサキ 155', '03-0000-0155', 13),
('REF156', 'Test Contact 156', 'テストレンラクサキ 156', '03-0000-0156', 13),
('REF157', 'Test Contact 157', 'テストレンラクサキ 157', '03-0000-0157', 13),
('REF158', 'Test Contact 158', 'テストレンラクサキ 158', '03-0000-0158', 13),
('REF159', 'Test Contact 159', 'テストレンラクサキ 159', '03-0000-0159', 13),
('REF160', 'Test Contact 160', 'テストレンラクサキ 160', '03-0000-0160', 13),
('REF161', 'Test Contact 161', 'テストレンラクサキ 161', '03-0000-0161', 13),
('REF162', 'Test Contact 162', 'テストレンラクサキ 162', '03-0000-0162', 13),
('REF163', 'Test Contact 163', 'テストレンラクサキ 163', '03-0000-0163', 13),
('REF164', 'Test Contact 164', 'テストレンラクサキ 164', '03-0000-0164', 13),
('REF165', 'Test Contact 165', 'テストレンラクサキ 165', '03-0000-0165', 13),
('REF166', 'Test Contact 166', 'テストレンラクサキ 166', '03-0000-0166', 13),
('REF167', 'Test Contact 167', 'テストレンラクサキ 167', '03-0000-0167', 13),
('REF168', 'Test Contact 168', 'テストレンラクサキ 168', '03-0000-0168', 13),
('REF169', 'Test Contact 169', 'テストレンラクサキ 169', '03-0000-0169', 13),
('REF170', 'Test Contact 170', 'テストレンラクサキ 170', '03-0000-0170', 13),
('REF171', 'Test Contact 171', 'テストレンラクサキ 171', '03-0000-0171', 13),
('REF172', 'Test Contact 172', 'テストレンラクサキ 172', '03-0000-0172', 13),
('REF173', 'Test Contact 173', 'テストレンラクサキ 173', '03-0000-0173', 13),
('REF174', 'Test Contact 174', 'テストレンラクサキ 174', '03-0000-0174', 13),
('REF175', 'Test Contact 175', 'テストレンラクサキ 175', '03-0000-0175', 13),
('REF176', 'Test Contact 176', 'テストレンラクサキ 176', '03-0000-0176', 13),
('REF177', 'Test Contact 177', 'テストレンラクサキ 177', '03-0000-0177', 13),
('REF178', 'Test Contact 178', 'テストレンラクサキ 178', '03-0000-0178', 13),
('REF179', 'Test Contact 179', 'テストレンラクサキ 179', '03-0000-0179', 13),
('REF180', 'Test Contact 180', 'テストレンラクサキ 180', '03-0000-0180', 13),
('REF181', 'Test Contact 181', 'テストレンラクサキ 181', '03-0000-0181', 13),
('REF182', 'Test Contact 182', 'テストレンラクサキ 182', '03-0000-0182', 13),
('REF183', 'Test Contact 183', 'テストレンラクサキ 183', '03-0000-0183', 13),
('REF184', 'Test Contact 184', 'テストレンラクサキ 184', '03-0000-0184', 13),
('REF185', 'Test Contact 185', 'テストレンラクサキ 185', '03-0000-0185', 13),
('REF186', 'Test Contact 186', 'テストレンラクサキ 186', '03-0000-0186', 13),
('REF187', 'Test Contact 187', 'テストレンラクサキ 187', '03-0000-0187', 13),
('REF188', 'Test Contact 188', 'テストレンラクサキ 188', '03-0000-0188', 13),
('REF189', 'Test Contact 189', 'テストレンラクサキ 189', '03-0000-0189', 13),
('REF190', 'Test Contact 190', 'テストレンラクサキ 190', '03-0000-0190', 13),
('REF191', 'Test Contact 191', 'テストレンラクサキ 191', '03-0000-0191', 13),
('REF192', 'Test Contact 192', 'テストレンラクサキ 192', '03-0000-0192', 13),
('REF193', 'Test Contact 193', 'テストレンラクサキ 193', '03-0000-0193', 13),
('REF194', 'Test Contact 194', 'テストレンラクサキ 194', '03-0000-0194', 13);

-- Added 95 Test contacts
