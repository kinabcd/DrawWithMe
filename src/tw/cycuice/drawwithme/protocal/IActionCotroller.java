package tw.cycuice.drawwithme.protocal;

import java.util.List;

public interface IActionCotroller {
  public void PushAction( Action newAction );

  public boolean HasNewAction();

  public List<Action> PullAction();
}
