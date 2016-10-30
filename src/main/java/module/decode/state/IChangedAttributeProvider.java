package module.decode.state;

import sample.Listener;

public interface IChangedAttributeProvider
{
  void setChangedAttributeListener(Listener<ChangedAttribute> listener);

  void removeChangedAttributeListener();
}
