package tracker.lift_log.Helpers;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import tracker.lift_log.R;

/**
 * Applies edge-to-edge display and pads toolbar / bottom ad container for system bars.
 */
public final class EdgeToEdgeHelper {
    private EdgeToEdgeHelper() {
    }

    public static void apply(Activity activity, View root) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);

        final View toolbar = root.findViewById(R.id.toolbar);
        final View adLayout = root.findViewById(R.id.ad_layout);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (toolbar != null) {
                toolbar.setPadding(
                        toolbar.getPaddingLeft(),
                        bars.top,
                        toolbar.getPaddingRight(),
                        toolbar.getPaddingBottom()
                );
            } else {
                v.setPadding(bars.left, bars.top, bars.right, v.getPaddingBottom());
            }
            if (adLayout != null) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) adLayout.getLayoutParams();
                if (lp != null) {
                    lp.bottomMargin = bars.bottom;
                    adLayout.setLayoutParams(lp);
                } else {
                    adLayout.setPadding(
                            adLayout.getPaddingLeft(),
                            adLayout.getPaddingTop(),
                            adLayout.getPaddingRight(),
                            bars.bottom
                    );
                }
            } else {
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bars.bottom);
            }
            return insets;
        });
    }
}
