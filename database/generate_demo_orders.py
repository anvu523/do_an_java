#!/usr/bin/env python3
"""Generate realistic demo order history for BrewPoint POS (2026-05-01 .. 2026-06-15)."""

import random
from datetime import date, datetime, timedelta

random.seed(20260615)

START = date(2026, 5, 1)
END = date(2026, 6, 15)

CASHIERS = [
    (2, "Nguyễn Thị Lan", 0.34),
    (3, "Trần Văn Minh", 0.33),
    (4, "Lê Hoàng An", 0.33),
]

PRODUCTS = [
    # product_id, code, name, sizes: [(code, name, price)]
    (1, "CF-DEN-DA", "Cà phê phin đen đá", [("STD", "Tiêu chuẩn", 25000)]),
    (2, "CF-SUA-DA", "Cà phê sữa đá", [("STD", "Tiêu chuẩn", 29000)]),
    (3, "BAC-XIU", "Bạc xỉu", [("STD", "Tiêu chuẩn", 32000)]),
    (4, "TRA-DAO-CAM-SA", "Trà đào cam sả", [("M", "Vừa (M)", 39000), ("L", "Lớn (L)", 49000)]),
    (5, "TRA-SEN-VANG", "Trà sen vàng", [("M", "Vừa (M)", 42000), ("L", "Lớn (L)", 52000)]),
    (6, "TRA-VAI", "Trà vải", [("M", "Vừa (M)", 39000), ("L", "Lớn (L)", 49000)]),
    (7, "TS-TC-DD", "Trà sữa trân châu đường đen", [("M", "Vừa (M)", 45000), ("L", "Lớn (L)", 55000)]),
    (8, "OLONG-SUA", "Ô long sữa", [("M", "Vừa (M)", 39000), ("L", "Lớn (L)", 49000)]),
    (9, "HONG-TRA-SUA", "Hồng trà sữa", [("M", "Vừa (M)", 39000), ("L", "Lớn (L)", 49000)]),
    (10, "MATCHA-LATTE", "Matcha latte", [("M", "Vừa (M)", 45000), ("L", "Lớn (L)", 55000)]),
    (11, "CACAO-SUA-DA", "Cacao sữa đá", [("M", "Vừa (M)", 39000), ("L", "Lớn (L)", 49000)]),
    (12, "MATCHA-DA-XAY", "Matcha đá xay", [("M", "Vừa (M)", 55000), ("L", "Lớn (L)", 65000)]),
]

PRODUCT_WEIGHTS = {
    7: 22, 4: 18, 3: 15, 2: 12, 8: 10,
    5: 8, 10: 7, 9: 6, 1: 5, 11: 4, 12: 3, 6: 3,
}

TOPPINGS = [
    (1, "TC-DEN", "Trân châu đen", 10000),
    (2, "TC-TRANG", "Trân châu trắng", 10000),
    (3, "THACH-CA-PHE", "Thạch cà phê", 8000),
    (4, "THACH-DUA", "Thạch dừa", 8000),
    (5, "PUDDING-TRUNG", "Pudding trứng", 8000),
    (6, "KEM-CHEESE", "Kem cheese", 12000),
    (7, "HAT-SEN", "Hạt sen", 10000),
]

TOPPING_WEIGHTS = [30, 12, 8, 6, 18, 4, 5]

PEAK_HOURS = list(range(11, 14)) + list(range(18, 22))
NORMAL_HOURS = [h for h in range(7, 22) if h not in PEAK_HOURS]


def pick_product():
    ids = list(PRODUCT_WEIGHTS.keys())
    weights = [PRODUCT_WEIGHTS[i] for i in ids]
    return random.choices(ids, weights=weights, k=1)[0]


def pick_size(product):
    sizes = PRODUCTS[product - 1][3]
    if len(sizes) == 1:
        return sizes[0]
    return sizes[0] if random.random() < 0.62 else sizes[1]


def pick_toppings():
    if random.random() < 0.66:
        return []
    count = 1 if random.random() < 0.78 else 2
    indices = random.choices(range(len(TOPPINGS)), weights=TOPPING_WEIGHTS, k=count)
    unique = []
    for idx in indices:
        if idx not in unique:
            unique.append(idx)
    if len(unique) < count:
        for idx in range(len(TOPPINGS)):
            if idx not in unique:
                unique.append(idx)
            if len(unique) == count:
                break
    return [TOPPINGS[i] for i in unique[:count]]


def pick_cashier():
    ids = [c[0] for c in CASHIERS]
    weights = [c[2] for c in CASHIERS]
    return random.choices(ids, weights=weights, k=1)[0]


def orders_for_day(d):
    base = 28 if d.weekday() < 5 else 48
    return base + random.randint(-4, 6)


def pick_hour():
    if random.random() < 0.62:
        return random.choice(PEAK_HOURS)
    return random.choice(NORMAL_HOURS)


def round_cash(total):
    denoms = [50000, 100000, 200000, 500000]
    for d in denoms:
        if d >= total:
            return d
    return ((total // 100000) + 1) * 100000


def sql_str(value):
    if value is None:
        return "NULL"
    return "'" + str(value).replace("'", "''") + "'"


def main():
    order_lines = []
    item_lines = []
    topping_lines = []
    order_id = 1
    item_id = 1
    topping_id = 1
    seq_by_day = {}

    d = START
    while d <= END:
        count = orders_for_day(d)
        seq_by_day[d] = 0
        for _ in range(count):
            seq_by_day[d] += 1
            hour = pick_hour()
            minute = random.randint(0, 59)
            second = random.randint(0, 59)
            order_time = datetime(d.year, d.month, d.day, hour, minute, second)
            code = "HD%04d%02d%02d%03d" % (d.year, d.month, d.day, seq_by_day[d])
            employee_id = pick_cashier()
            cash = random.random() < 0.72
            payment = "CASH" if cash else "MANUAL_BANK_TRANSFER"

            line_count = 1 if random.random() < 0.78 else (2 if random.random() < 0.85 else 3)
            subtotal = 0
            order_items = []

            for _ in range(line_count):
                pid = pick_product()
                prod = PRODUCTS[pid - 1]
                size = pick_size(pid)
                qty = 1 if random.random() < 0.88 else 2
                base_price = size[2]
                unit_price = base_price
                tops = pick_toppings()
                for t in tops:
                    unit_price += t[3]
                line_total = unit_price * qty
                subtotal += line_total
                order_items.append((pid, prod, size, qty, base_price, unit_price, line_total, tops))

            total = subtotal
            received = None
            change = None
            if cash:
                received = round_cash(total)
                change = received - total

            order_lines.append(
                "(%d, %s, %d, %s, 'COMPLETED', %s, %d, 0, %d, %s, %s)"
                % (
                    order_id,
                    sql_str(code),
                    employee_id,
                    sql_str(order_time.strftime("%Y-%m-%d %H:%M:%S")),
                    sql_str(payment),
                    subtotal,
                    total,
                    str(received) if received is not None else "NULL",
                    str(change) if change is not None else "NULL",
                )
            )

            for pid, prod, size, qty, base_price, unit_price, line_total, tops in order_items:
                item_lines.append(
                    "(%d, %d, %d, %s, %s, %s, %s, %d, %d, %d, %d, NULL)"
                    % (
                        item_id,
                        order_id,
                        pid,
                        sql_str(prod[1]),
                        sql_str(prod[2]),
                        sql_str(size[0]),
                        sql_str(size[1]),
                        base_price,
                        unit_price,
                        qty,
                        line_total,
                    )
                )
                for t in tops:
                    topping_lines.append(
                        "(%d, %d, %d, %s, %s, %d)"
                        % (topping_id, item_id, t[0], sql_str(t[1]), sql_str(t[2]), t[3])
                    )
                    topping_id += 1
                item_id += 1
            order_id += 1
        d += timedelta(days=1)

    out = []
    out.append("-- BrewPoint POS demo order history")
    out.append("-- Range: 2026-05-01 .. 2026-06-15")
    out.append("-- Run AFTER drink_store.sql: mysql ... < seed_demo_orders.sql")
    out.append("")
    out.append("USE brewpoint_pos;")
    out.append("SET NAMES utf8mb4;")
    out.append("")
    out.append("DELETE FROM order_item_toppings;")
    out.append("DELETE FROM order_items;")
    out.append("DELETE FROM orders;")
    out.append("")
    out.append("INSERT INTO orders (order_id, order_code, employee_id, order_time, status, payment_method, subtotal, discount_amount, total_amount, received_amount, change_amount) VALUES")
    out.append(",\n".join(order_lines) + ";")
    out.append("")
    out.append("INSERT INTO order_items (order_item_id, order_id, product_id, product_code_snapshot, product_name_snapshot, size_code_snapshot, size_name_snapshot, base_price_snapshot, unit_price, quantity, line_total, note) VALUES")
    out.append(",\n".join(item_lines) + ";")
    out.append("")
    out.append("INSERT INTO order_item_toppings (order_item_topping_id, order_item_id, topping_id, topping_code_snapshot, topping_name_snapshot, extra_price_snapshot) VALUES")
    out.append(",\n".join(topping_lines) + ";")
    out.append("")
    out.append("-- Orders: %d | Items: %d | Toppings: %d" % (order_id - 1, item_id - 1, topping_id - 1))

    path = "seed_demo_orders.sql"
    with open(path, "w", encoding="utf-8") as f:
        f.write("\n".join(out))
    print("Wrote %s (%d orders)" % (path, order_id - 1))


if __name__ == "__main__":
    main()
