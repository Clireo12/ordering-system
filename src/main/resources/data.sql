-- 插入测试用户数据
INSERT INTO tb_user (user_name, password, phone, coin, coupons)
VALUES ('用户1', '123456', '13800000001', 1100, 5),
       ('用户2', '123456', '13800000002', 500, 3),
       ('用户3', '123456', '13800000003', 300, 1);

-- 插入分类数据
INSERT INTO `categories` (`id`, `name`, `display_order`)
VALUES (1, '当季新品', 1),
       (2, '本店热销', 2),
       (3, '清爽果茶', 3),
       (4, '新鲜冰淇淋', 4),
       (5, '醇香奶茶', 5),
       (6, '鲜萃咖啡', 6),
       (7, '轻乳系列', 7),
       (8, '芝士奶盖', 8),
       (9, '原叶纯茶', 9);

-- 插入商品数据
INSERT INTO `products` (`category_id`, `image`, `name`, `description`, `price`)
VALUES (1, '/static/images/products/xinpin1.jpg', '柠檬红茶霸气桶', '大大大大大，大桶更霸气', 7.90),
       (1, '/static/images/products/xinpin2.jpg', '青梅冰茶', '夏日解渴，青梅好喝', 6.00),
       (2, '/static/images/products/rexiao1.jpg', '冰鲜柠檬水', '大片柠檬看得见，现切超新鲜', 5.00),
       (2, '/static/images/products/rexiao2.jpg', '茉莉奶绿', '好一朵美丽的茉莉花', 7.00),
       (2, '/static/images/products/rexiao3.jpg', '棒打鲜橙', '大片橙子真给力', 7.00),
       (2, '/static/images/products/naicha1.jpg', '珍珠奶茶', '珍珠Q弹，奶茶经典', 7.00),
       (2, '/static/images/products/guocha1.jpg', '芋圆葡萄', '大颗葡萄肉', 9.00),
       (3, '/static/images/products/rexiao1.jpg', '冰鲜柠檬水', '大片柠檬看得见，现切超新鲜', 5.00),
       (3, '/static/images/products/rexiao3.jpg', '棒打鲜橙', '大片橙子真给力', 7.00),
       (3, '/static/images/products/guocha1.jpg', '芋圆葡萄', '大颗葡萄肉', 9.00),
       (3, '/static/images/products/guocha2.jpg', '满杯百香果', '满杯百香果，酸酸甜甜', 8.00),
       (3, '/static/images/products/guocha3.jpg', '蜜桃四季春', '蜜桃四季春', 7.00),
       (3, '/static/images/products/guocha4.jpg', '草莓啵啵', '草莓啵啵，甜蜜啵一个', 10.00),
       (4, '/static/images/products/bingqilin1.jpg', '雪王大圣代（草莓）', '草莓大圣代', 7.00),
       (4, '/static/images/products/bingqilin2.jpg', '雪王大圣代（奥利奥饼干风味）', '经典回归，奥利奥饼干风味', 7.00),
       (4, '/static/images/products/bingqilin3.jpg', '草莓摇摇奶昔', '一杯有三宝，喝前摇一摇', 7.00),
       (5, '/static/images/products/naicha1.jpg', '珍珠奶茶', '珍珠Q弹，奶茶经典', 7.00),
       (5, '/static/images/products/naicha2.jpg', '椰果奶茶', '爽脆椰果粒，奶茶甜蜜蜜', 8.00),
       (5, '/static/images/products/naicha3.jpg', '双拼奶茶（升级）', '布丁原料升级', 8.00),
       (6, '/static/images/products/kafei1.jpg', '美式咖啡（升级版）', '纯正美式，浓郁香气', 6.00),
       (6, '/static/images/products/kafei2.jpg', '葡萄冰美式', '真实葡萄果汁', 7.00),
       (6, '/static/images/products/kafei3.jpg', '拿铁咖啡（新）', '咖啡醇厚', 8.00),
       (6, '/static/images/products/kafei4.jpg', '椰椰拿铁', '椰香四溢，清甜不腻', 9.00),
       (7, '/static/images/products/qingru1.jpg', '港式杨枝甘露（新）', '升级杨枝甘露，大果肉厚椰乳', 7.00),
       (7, '/static/images/products/rexiao2.jpg', '茉莉奶绿', '好一朵美丽的茉莉花', 7.00),
       (8, '/static/images/products/zhishi1.jpg', '芝士奶盖四季春', '绵绵咸芝士', 9.00),
       (8, '/static/images/products/zhishi2.jpg', '芝士奶盖红茶', '绵绵咸芝士，红茶喝不腻', 9.00),
       (8, '/static/images/products/zhishi3.jpg', '芝士奶盖绿茶', '绵绵咸芝士', 9.00),
       (9, '/static/images/products/chuncha1.jpg', '茉莉绿茶', '花香茶香，双倍清香', 5.00),
       (9, '/static/images/products/chuncha2.jpg', '蜜雪原叶红茶', '蜜雪红茶，原叶好茶', 5.00),
       (9, '/static/images/products/chuncha3.jpg', '高山四季春茶', '主辅料：四季春茶', 5.00);

-- 为用户1创建购物车并添加商品
INSERT INTO shopping_carts (user_id) VALUES (1);

-- 为用户1添加购物车商品
INSERT INTO cart_items (cart_id, product_id, quantity, temperature, sugar)
VALUES
    (1, 1, 2, '正常冰', '正常糖'),  -- 柠檬红茶霸气桶
    (1, 3, 1, '少冰', '七分糖'),    -- 冰鲜柠檬水
    (1, 6, 3, '热', '五分糖');      -- 珍珠奶茶

-- 为用户2创建购物车并添加商品
INSERT INTO shopping_carts (user_id) VALUES (2);

-- 为用户2添加购物车商品
INSERT INTO cart_items (cart_id, product_id, quantity, temperature, sugar)
VALUES
    (2, 2, 1, '正常冰', '无糖'),    -- 青梅冰茶
    (2, 5, 2, '少冰', '三分糖'),    -- 棒打鲜橙
    (2, 8, 1, '正常冰', '正常糖');  -- 冰鲜柠檬水

-- 为用户3创建购物车并添加商品
INSERT INTO shopping_carts (user_id) VALUES (3);

-- 为用户3添加购物车商品
INSERT INTO cart_items (cart_id, product_id, quantity, temperature, sugar)
VALUES
    (3, 7, 1, '少冰', '七分糖');
