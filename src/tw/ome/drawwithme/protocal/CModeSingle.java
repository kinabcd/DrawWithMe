package tw.ome.drawwithme.protocal;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tw.ome.drawwithme.DrawSurface;

public class CModeSingle implements IActionCotroller {

    List<Action> mActions;

    public CModeSingle() {
        mActions = Collections.synchronizedList(new LinkedList<Action>());
    }

    public void PushAction(Action newAction) {
        mActions.add(newAction);

        DrawSurface.GetInstance().RequireRedraw();
    }

    public boolean HasNewAction() {
        if (!mActions.isEmpty())
            return true;
        return false;
    }

    public List<Action> PullAction() {
        List<Action> relist = new LinkedList<Action>();
        while (!mActions.isEmpty()) {
            Action exe = mActions.remove(0);
            relist.add(exe);
        }

        return relist;
    }
}
