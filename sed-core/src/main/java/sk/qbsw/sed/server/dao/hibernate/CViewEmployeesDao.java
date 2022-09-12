package sk.qbsw.sed.server.dao.hibernate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import sk.qbsw.core.dao.hibernate.AHibernateDao;
import sk.qbsw.sed.framework.report.model.CReportModel;
import sk.qbsw.sed.server.dao.IViewEmployeesDao;
import sk.qbsw.sed.server.model.report.CEmployeesDataModel;
import sk.qbsw.sed.server.model.report.CViewEmployees;

@Repository
public class CViewEmployeesDao extends AHibernateDao<CViewEmployees> implements IViewEmployeesDao {

	@SuppressWarnings("unchecked")
	public List<CReportModel> findUsersRecordsForEmployeesReport(Long clientId, Boolean onlyValid) {

		final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CViewEmployees.class);

		criteria.add(Restrictions.eq("client", clientId));

		if (onlyValid) {
			criteria.add(Property.forName("valid").eq(Boolean.TRUE));
		}

		return this.convertModel(this.groupModel(criteria.list()));
	}

	private Map<Long, LinkedList<CEmployeesDataModel>> groupModel(final List<CViewEmployees> data) {
		final Map<Long, LinkedList<CEmployeesDataModel>> group = new LinkedHashMap<>();

		for (final CViewEmployees record : data) {
			LinkedList<CEmployeesDataModel> model = new LinkedList<>();

			model = new LinkedList<>();

			CEmployeesDataModel singleModel = new CEmployeesDataModel();

			singleModel.setEmpCode(record.getEmpCode());
			singleModel.setName(record.getName());
			singleModel.setIdentificationNumber(record.getIdentificationNumber());
			singleModel.setCrn(record.getCrn());
			singleModel.setVatin(record.getVatin());
			singleModel.setBirthDate(record.getBirthDate());
			singleModel.setDescription(record.getDescription());
			singleModel.setWorkStartDate(record.getWorkStartDate());
			singleModel.setWorkEndDate(record.getWorkEndDate());
			singleModel.setPositionName(record.getPositionName());
			singleModel.setTitle(record.getTitle());
			singleModel.setBirthPlace(record.getBirthPlace());
			singleModel.setResidentIdentityCardNumber(record.getResidentIdentityCardNumber());
			singleModel.setContactStreet(record.getContactStreet());
			singleModel.setContactStreetNumber(record.getContactStreetNumber());
			singleModel.setContactZip(record.getContactZip());
			singleModel.setCity(record.getCity());
			singleModel.setHealthInsuranceCompany(record.getHealthInsuranceCompany());
			singleModel.setBankAccountNumber(record.getBankAccountNumber());
			singleModel.setBankInstitution(record.getBankInstitution());
			singleModel.setCriminalRecords(record.getCriminalRecords() ? 1 : 0);
			singleModel.setRecMedicalCheck(record.getRecMedicalCheck() ? 1 : 0);
			singleModel.setMultisportCard(record.getMultisportCard() ? 1 : 0);

			model.add(singleModel);

			group.put(record.getUserId(), model);
		}

		return group;
	}

	private List<CReportModel> convertModel(final Map<Long, LinkedList<CEmployeesDataModel>> group) {
		final List<CReportModel> reportModel = new ArrayList<>(group.size());
		for (final Long id : group.keySet()) {
			for (final CEmployeesDataModel model : group.get(id)) {
				final CReportModel rowModel = new CReportModel();

				// 0 = id / emp_code (id / kód zamestnanca)
				rowModel.setValue(0, model.getEmpCode());
				// 1 = name surname (meno a priezvisko)
				rowModel.setValue(1, model.getName());
				// 2 = identification number (rodné číslo)
				rowModel.setValue(2, model.getIdentificationNumber());
				// 3 = crn (IČO)
				rowModel.setValue(3, model.getCrn());
				// 4 = vatin (DIČ)
				rowModel.setValue(4, model.getVatin());
				// 5 = birth day (dátum narodenia)
				rowModel.setValue(5, model.getBirthDate());
				// 6 = description (Typ pracovného pomeru (PP))
				rowModel.setValue(6, model.getDescription());
				// 7 = work start date (začiatok pracovného pomeru)
				rowModel.setValue(7, model.getWorkStartDate());
				// 8 = work end date (koniec pracovného pomeru)
				rowModel.setValue(8, model.getWorkEndDate());
				// 9 = position name (zaradenie/pozícia)
				rowModel.setValue(9, model.getPositionName());
				// 10 = title (titul)
				rowModel.setValue(10, model.getTitle());
				// 11 = birth place (miesto narodenia)
				rowModel.setValue(11, model.getBirthPlace());
				// 12 = resident identity card number (číslo OP)
				rowModel.setValue(12, model.getResidentIdentityCardNumber());
				// 13 = contact street (TP ulica)
				rowModel.setValue(13, model.getContactStreet());
				// 14 = contact street number (TP číslo domu)
				rowModel.setValue(14, model.getContactStreetNumber());
				// 15 = contact zip (TP PSČ)
				rowModel.setValue(15, model.getContactZip());
				// 16 = city (TP mesto)
				rowModel.setValue(16, model.getCity());
				// 17 = health insurance company (zdravotná poisťovňa)
				rowModel.setValue(17, model.getHealthInsuranceCompany());
				// 18 = bank account number (číslo účtu)
				rowModel.setValue(18, model.getBankAccountNumber());
				// 19 = bank institution (banka)
				rowModel.setValue(19, model.getBankInstitution());
				// 20 = criminal records (výpis z registra trestov)
				rowModel.setValue(20, model.getCriminalRecords());
				// 21 = recruit medical check (vstupná lekárska prehliadka)
				rowModel.setValue(21, model.getRecMedicalCheck());
				// 22 = multicport card (karta multisport)
				rowModel.setValue(22, model.getMultisportCard());

				reportModel.add(rowModel);
			}
		}
		return reportModel;
	}
}
