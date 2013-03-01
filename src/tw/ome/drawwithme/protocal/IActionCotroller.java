package tw.ome.drawwithme.protocal;

import java.util.List;

public interface IActionCotroller {
  public void PushAction( Action newAction );

  public List<Action> PullAction();
}
