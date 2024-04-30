/**
 */
package documentschema.impl;

import documentschema.Aggregate;
import documentschema.DocumentschemaPackage;
import documentschema.Property;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Aggregate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link documentschema.impl.AggregateImpl#getAggregates <em>Aggregates</em>}</li>
 *   <li>{@link documentschema.impl.AggregateImpl#isIsMany <em>Is Many</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AggregateImpl extends PropertyImpl implements Aggregate {
	/**
	 * The cached value of the '{@link #getAggregates() <em>Aggregates</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAggregates()
	 * @generated
	 * @ordered
	 */
	protected Property aggregates;

	/**
	 * The default value of the '{@link #isIsMany() <em>Is Many</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsMany()
	 * @generated
	 * @ordered
	 */
	protected static final boolean IS_MANY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isIsMany() <em>Is Many</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isIsMany()
	 * @generated
	 * @ordered
	 */
	protected boolean isMany = IS_MANY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AggregateImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DocumentschemaPackage.Literals.AGGREGATE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Property getAggregates() {
		return aggregates;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAggregates(Property newAggregates, NotificationChain msgs) {
		Property oldAggregates = aggregates;
		aggregates = newAggregates;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DocumentschemaPackage.AGGREGATE__AGGREGATES, oldAggregates, newAggregates);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAggregates(Property newAggregates) {
		if (newAggregates != aggregates) {
			NotificationChain msgs = null;
			if (aggregates != null)
				msgs = ((InternalEObject)aggregates).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DocumentschemaPackage.AGGREGATE__AGGREGATES, null, msgs);
			if (newAggregates != null)
				msgs = ((InternalEObject)newAggregates).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DocumentschemaPackage.AGGREGATE__AGGREGATES, null, msgs);
			msgs = basicSetAggregates(newAggregates, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DocumentschemaPackage.AGGREGATE__AGGREGATES, newAggregates, newAggregates));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isIsMany() {
		return isMany;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIsMany(boolean newIsMany) {
		boolean oldIsMany = isMany;
		isMany = newIsMany;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DocumentschemaPackage.AGGREGATE__IS_MANY, oldIsMany, isMany));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DocumentschemaPackage.AGGREGATE__AGGREGATES:
				return basicSetAggregates(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DocumentschemaPackage.AGGREGATE__AGGREGATES:
				return getAggregates();
			case DocumentschemaPackage.AGGREGATE__IS_MANY:
				return isIsMany();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DocumentschemaPackage.AGGREGATE__AGGREGATES:
				setAggregates((Property)newValue);
				return;
			case DocumentschemaPackage.AGGREGATE__IS_MANY:
				setIsMany((Boolean)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case DocumentschemaPackage.AGGREGATE__AGGREGATES:
				setAggregates((Property)null);
				return;
			case DocumentschemaPackage.AGGREGATE__IS_MANY:
				setIsMany(IS_MANY_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case DocumentschemaPackage.AGGREGATE__AGGREGATES:
				return aggregates != null;
			case DocumentschemaPackage.AGGREGATE__IS_MANY:
				return isMany != IS_MANY_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (isMany: ");
		result.append(isMany);
		result.append(')');
		return result.toString();
	}

} //AggregateImpl
