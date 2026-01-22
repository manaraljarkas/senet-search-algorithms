import java.util.*;

public class MoveRules {

    private static final int BARRIER = 26;

    /* ====== MOVE GENERATION ====== */

    public static List<Move> generateMoves(Board b, int dice) {
        List<Move> moves = new ArrayList<>();
        Player p = b.getCurrentPlayer();
        int pv = p.getValue();

        boolean hasPending = b.isPendingExit(p);

        for (int i = 1; i <= 30; i++) {
            if (b.getPieceAt(i) != pv) continue;

            int to = i + dice;
            if (to > 30) continue;
            if (crossesBarrier(i, to)) continue;

            int target = b.getPieceAt(to);
            if (target == pv) continue;

            moves.add(new Move(i, to));
        }

        // ➕ إذا في حجر على 30 → أضيف خيار الخروج
        if (hasPending) {
            moves.add(new Move(30, 31));
        }

        // ➕ إذا في حجر على 28 وحصل على 3 عصي → أضيف خيار الخروج
        if (b.getPieceAt(28) == pv && dice == 3) {
            moves.add(new Move(28, 31));
        }

        // ➕ إذا في حجر على 29 وحصل على 2 عصي → أضيف خيار الخروج
        if (b.getPieceAt(29) == pv && dice == 2) {
            moves.add(new Move(29, 31));
        }

        return moves;
    }

    /* ====== APPLY MOVE ====== */

    public static void apply(Board b, Move m) {
        apply(b, m, 0); // Default dice value for backward compatibility
    }

    public static void apply(Board b, Move m, int dice) {
        Player p = b.getCurrentPlayer();
        int pv = p.getValue();

        // 1️⃣ إذا في حجر معلّق ولم يتم اختيار الخروج → رجوع أولًا
        if (b.isPendingExit(p) && !(m.from == 30 && m.to == 31)) {
            sendBackFromExit(b, p);
        }

        // 2️⃣ خروج نهائي (من 30، 28، أو 29)
        if ((m.from == 30 && m.to == 31) || 
            (m.from == 28 && m.to == 31) || 
            (m.from == 29 && m.to == 31)) {
            b.removePieceAt(m.from);
            b.pieceOut(p);
            b.setPendingExit(p, false);
            // إزالة علامات الفحص إذا كانت موجودة
            if (m.from == 28) {
                b.setNeedsCheck28(p, false);
            }
            if (m.from == 29) {
                b.setNeedsCheck29(p, false);
            }
            b.switchPlayer();
            return;
        }

        // 3️⃣ إزالة الحجر من المصدر
        b.removePieceAt(m.from);

        // 4️⃣ الوصول إلى 30 → تعليق
        if (m.to == 30) {
            b.setPieceAt(30, pv);
            b.setPendingExit(p, true);
            b.switchPlayer();
            return;
        }

        // 5️⃣ ماء → رجوع
        if (m.to == 27) {
            rebirth(b, pv);
            b.switchPlayer();
            return;
        }

        // 6️⃣ تبادل
        int target = b.getPieceAt(m.to);
        if (target != 0 && target != pv) {
            b.setPieceAt(m.from, target);
        }

        // 7️⃣ وضع الحجر
        b.setPieceAt(m.to, pv);

        // 8️⃣ علامة للفحص في الدور القادم للمواضع 28 و 29
        if (m.to == 28) {
            // وضع علامة للتحقق في الدور القادم
            b.setNeedsCheck28(p, true);
        }

        if (m.to == 29) {
            // وضع علامة للتحقق في الدور القادم
            b.setNeedsCheck29(p, true);
        }

        // 9️⃣ تبديل الدور
        b.switchPlayer();
    }

    /* ====== HELPERS ====== */

    private static boolean crossesBarrier(int from, int to) {
        for (int i = from + 1; i < to; i++) {
            if (i == BARRIER) return true;
        }
        return false;
    }
    private static void sendBackFromExit(Board b, Player p) {
        int pv = p.getValue();

        b.removePieceAt(30);

        for (int i = 15; i >= 1; i--) {
            if (b.getPieceAt(i) == 0) {
                b.setPieceAt(i, pv);
                break;
            }
        }

        b.setPendingExit(p, false);
    }


    private static void rebirth(Board b, int pv) {
        for (int i = 15; i >= 1; i--) {
            if (b.getPieceAt(i) == 0) {
                b.setPieceAt(i, pv);
                return;
            }
        }
        b.setPieceAt(15, pv);
    }

    public static void sendBackFromSpecialPosition(Board b, Player p, int fromPosition) {
        int pv = p.getValue();

        // إزالة الحجر من الموضع الخاص (28 أو 29)
        b.removePieceAt(fromPosition);

        // البحث عن أقرب موضع متاح للخلف من 15
        for (int i = 15; i >= 1; i--) {
            if (b.getPieceAt(i) == 0) {
                b.setPieceAt(i, pv);
                return;
            }
        }

        // إذا لم يوجد موضع متاح، ضع الحجر في 15 (حتى لو كان مشغولاً)
        b.setPieceAt(15, pv);
    }
}

