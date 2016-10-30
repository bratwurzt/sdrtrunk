package sample.real;

import sample.Listener;

public interface IUnFilteredRealBufferProvider
{
  void setUnFilteredRealBufferListener(Listener<RealBuffer> listener);

  void removeUnFilteredRealBufferListener();
}
