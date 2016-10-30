package sample.real;

import sample.Listener;

public interface IFilteredRealBufferProvider
{
  void setFilteredRealBufferListener(Listener<RealBuffer> listener);

  void removeFilteredRealBufferListener();
}
