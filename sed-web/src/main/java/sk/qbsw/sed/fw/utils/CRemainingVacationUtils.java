package sk.qbsw.sed.fw.utils;

import java.text.MessageFormat;

/**
 * 
 * @author lobb
 *
 */
public class CRemainingVacationUtils {

	private CRemainingVacationUtils() {
		// Auto-generated constructor stub
	}

	/**
	 * 
	 * @param vacation
	 * @return
	 */
	public static String getText(Double vacation) {
		if (vacation != null) {
			String vacationString = vacation.toString();
			vacationString = vacationString.replace('.', ',');

			if (Double.valueOf(0).equals(vacation)) {
				// 0 = Už žiadna dovolenka
				return CStringResourceReader.read("remainingVacationDays.zero");
			} else if (vacation % 1 > 0) {
				// Pre stavy s ½ dňom:
				if (vacation < 5) {
					// 0,5 – 4,5 = Už len <n> dňa dovolenky
					return MessageFormat.format(CStringResourceReader.read("remainingVacationDays.justHalf"), vacationString);
				} else {
					// 5,5 + = Ešte <n> dňa dovolenky
					return MessageFormat.format(CStringResourceReader.read("remainingVacationDays.stillHalf"), vacationString);
				}
			} else if (Double.valueOf(1).equals(vacation)) {
				// 1 = Už len 1 deň dovolenky
				return CStringResourceReader.read("remainingVacationDays.one");
			} else if (vacation < 5) {
				// 2-4 Už len <n> dni dovolenky
				return MessageFormat.format(CStringResourceReader.read("remainingVacationDays.just"), vacation);
			} else {
				// 5+ = Ešte <n> dní dovolenky
				return MessageFormat.format(CStringResourceReader.read("remainingVacationDays.still"), vacation);
			}
		} else {
			return "";
		}
	}
}
