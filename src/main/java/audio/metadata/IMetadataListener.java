package audio.metadata;

import sample.Listener;

public interface IMetadataListener
{
  Listener<Metadata> getMetadataListener();
}
