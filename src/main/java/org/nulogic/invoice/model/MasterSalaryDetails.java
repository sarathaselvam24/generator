package org.nulogic.invoice.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "mastersalarydetails")
public class MasterSalaryDetails {
	
	@Id
	private String empid;

	@Column(name = "basicpay")
	private BigDecimal basicpay;

	@Column(name = "houseallowance")
	private BigDecimal houseallowance;

	@Column(name = "specialallowance")
	private BigDecimal specialallowance;

	@Column(name = "providentfund")
	private BigDecimal providentfund;
	
	@Column(name = "shift")
	private String shift;

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public BigDecimal getBasicpay() {
		return basicpay;
	}

	public void setBasicpay(BigDecimal basicpay) {
		this.basicpay = basicpay;
	}

	public BigDecimal getHouseallowance() {
		return houseallowance;
	}

	public void setHouseallowance(BigDecimal houseallowance) {
		this.houseallowance = houseallowance;
	}

	public BigDecimal getSpecialallowance() {
		return specialallowance;
	}

	public void setSpecialallowance(BigDecimal specialallowance) {
		this.specialallowance = specialallowance;
	}

	public BigDecimal getProvidentfund() {
		return providentfund;
	}

	public void setProvidentfund(BigDecimal providentfund) {
		this.providentfund = providentfund;
	}

	public String getShift() {
		return shift;
	}

	public void setShift(String shift) {
		this.shift = shift;
	}
	
	

}
