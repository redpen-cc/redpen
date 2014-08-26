package cc.redpen.validator;

/**
 * This interface provides the method which runs before validate method in Validator class.
 *
 * @param <E> input Document block class
 */
public interface PreProcessor<E> {
    /**
     * Process input blocks before run validation. This method is used to store
     * the information needed to run Validator before the validation process.
     *
     * @param block input block
     */
    void preprocess(E block);
}
