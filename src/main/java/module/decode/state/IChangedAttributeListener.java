package module.decode.state;

import sample.Listener;

public interface IChangedAttributeListener
{
  Listener<ChangedAttribute> getChangedAttributeListener();
}
