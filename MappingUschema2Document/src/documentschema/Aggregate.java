/**
 */
package documentschema;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Aggregate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link documentschema.Aggregate#getAggregates <em>Aggregates</em>}</li>
 *   <li>{@link documentschema.Aggregate#isIsMany <em>Is Many</em>}</li>
 * </ul>
 *
 * @see documentschema.DocumentschemaPackage#getAggregate()
 * @model
 * @generated
 */
public interface Aggregate extends Property {
	/**
	 * Returns the value of the '<em><b>Aggregates</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Aggregates</em>' containment reference.
	 * @see #setAggregates(Property)
	 * @see documentschema.DocumentschemaPackage#getAggregate_Aggregates()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Property getAggregates();

	/**
	 * Sets the value of the '{@link documentschema.Aggregate#getAggregates <em>Aggregates</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Aggregates</em>' containment reference.
	 * @see #getAggregates()
	 * @generated
	 */
	void setAggregates(Property value);

	/**
	 * Returns the value of the '<em><b>Is Many</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Many</em>' attribute.
	 * @see #setIsMany(boolean)
	 * @see documentschema.DocumentschemaPackage#getAggregate_IsMany()
	 * @model
	 * @generated
	 */
	boolean isIsMany();

	/**
	 * Sets the value of the '{@link documentschema.Aggregate#isIsMany <em>Is Many</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Many</em>' attribute.
	 * @see #isIsMany()
	 * @generated
	 */
	void setIsMany(boolean value);

} // Aggregate
