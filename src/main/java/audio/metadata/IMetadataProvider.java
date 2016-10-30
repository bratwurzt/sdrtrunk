package audio.metadata;

import sample.Listener;

public interface IMetadataProvider
{
  void setMetadataListener(Listener<Metadata> listener);

  void removeMetadataListener();
}
