package filter;

public interface IFilter<T>
{
  /**
   * Generic filter method.
   *
   * @param message - message to filter
   * @return - true if the message passes the filter
   */
  boolean passes(T t);

  /**
   * Indicates if the filter can process (filter) the object
   *
   * @param message - candidate message for filtering
   * @return - true if the filter is capable of filtering the message
   */
  boolean canProcess(T t);

  /**
   * Indicates if this filter is enabled to evaluate messages
   */
  boolean isEnabled();

  /**
   * Sets the enabled state of the filter
   */
  void setEnabled(boolean enabled);

  /**
   * Display name for the filter
   */
  String getName();
}
