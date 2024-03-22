/**
 */
package uschema.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import uschema.DataType;
import uschema.PMap;
import uschema.PrimitiveType;
import uschema.UschemaPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>PMap</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link uschema.impl.PMapImpl#getKeyType <em>Key Type</em>}</li>
 *   <li>{@link uschema.impl.PMapImpl#getValueType <em>Value Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PMapImpl extends DataTypeImpl implements PMap {
	/**
	 * The cached value of the '{@link #getKeyType() <em>Key Type</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKeyType()
	 * @generated
	 * @ordered
	 */
	protected PrimitiveType keyType;

	/**
	 * The cached value of the '{@link #getValueType() <em>Value Type</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValueType()
	 * @generated
	 * @ordered
	 */
	protected DataType valueType;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PMapImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return UschemaPackage.Literals.PMAP;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PrimitiveType getKeyType() {
		return keyType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetKeyType(PrimitiveType newKeyType, NotificationChain msgs) {
		PrimitiveType oldKeyType = keyType;
		keyType = newKeyType;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, UschemaPackage.PMAP__KEY_TYPE, oldKeyType, newKeyType);
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
	public void setKeyType(PrimitiveType newKeyType) {
		if (newKeyType != keyType) {
			NotificationChain msgs = null;
			if (keyType != null)
				msgs = ((InternalEObject)keyType).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - UschemaPackage.PMAP__KEY_TYPE, null, msgs);
			if (newKeyType != null)
				msgs = ((InternalEObject)newKeyType).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - UschemaPackage.PMAP__KEY_TYPE, null, msgs);
			msgs = basicSetKeyType(newKeyType, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UschemaPackage.PMAP__KEY_TYPE, newKeyType, newKeyType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DataType getValueType() {
		return valueType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetValueType(DataType newValueType, NotificationChain msgs) {
		DataType oldValueType = valueType;
		valueType = newValueType;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, UschemaPackage.PMAP__VALUE_TYPE, oldValueType, newValueType);
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
	public void setValueType(DataType newValueType) {
		if (newValueType != valueType) {
			NotificationChain msgs = null;
			if (valueType != null)
				msgs = ((InternalEObject)valueType).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - UschemaPackage.PMAP__VALUE_TYPE, null, msgs);
			if (newValueType != null)
				msgs = ((InternalEObject)newValueType).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - UschemaPackage.PMAP__VALUE_TYPE, null, msgs);
			msgs = basicSetValueType(newValueType, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UschemaPackage.PMAP__VALUE_TYPE, newValueType, newValueType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case UschemaPackage.PMAP__KEY_TYPE:
				return basicSetKeyType(null, msgs);
			case UschemaPackage.PMAP__VALUE_TYPE:
				return basicSetValueType(null, msgs);
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
			case UschemaPackage.PMAP__KEY_TYPE:
				return getKeyType();
			case UschemaPackage.PMAP__VALUE_TYPE:
				return getValueType();
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
			case UschemaPackage.PMAP__KEY_TYPE:
				setKeyType((PrimitiveType)newValue);
				return;
			case UschemaPackage.PMAP__VALUE_TYPE:
				setValueType((DataType)newValue);
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
			case UschemaPackage.PMAP__KEY_TYPE:
				setKeyType((PrimitiveType)null);
				return;
			case UschemaPackage.PMAP__VALUE_TYPE:
				setValueType((DataType)null);
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
			case UschemaPackage.PMAP__KEY_TYPE:
				return keyType != null;
			case UschemaPackage.PMAP__VALUE_TYPE:
				return valueType != null;
		}
		return super.eIsSet(featureID);
	}

} //PMapImpl
