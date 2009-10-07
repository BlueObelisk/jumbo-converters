package org.xmlcml.cml.converters.cif;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.cml.base.CMLConstants;

/** this class will be transferred to a dictionary soon.
*
* @author pmr
*
*/
public class CIFCategory implements CMLConstants {
	
	/** suffix of category entries.
	 */
	public final static String SUFFIX = S_UNDER+S_LSQUARE+S_RSQUARE;
	
	/** chemical_name */
	public final static CIFCategory CHEMICAL =
		new CIFCategory(
				"chemical_name",
				new String[]{
						"_chemical_name_systematic",
						"_chemical_name_common"
				},null);
    /** chemical_formula */
	public final static CIFCategory CHEMICAL_FORMULA =
		new CIFCategory(
				"chemical_formula",
				new String[]{
						"_chemical_formula_moiety",
						"_chemical_formula_sum",
						"_chemical_formula_iupac",
						"_chemical_formula_weight"
				},
				null);
    /** symmetry */
	public final static CIFCategory SYMMETRY =
		new CIFCategory(
				"symmetry",
				new String[]{
						"_symmetry_cell_setting",
						"_symmetry_space_group_name_H-M"
				},
				null);
    /** cell */
	public final static CIFCategory CELL =
		new CIFCategory(
				"cell",
				new String[]{
						"_cell_length_a",
						"_cell_length_b",
						"_cell_length_c",
						"_cell_angle_alpha",
						"_cell_angle_beta",
						"_cell_angle_gamma",
						"_cell_volume",
						"_cell_formula_units_Z",
						"_symmetry_space_group_name_H-M"
				},
				null);
    /** diffrn */
	public final static CIFCategory DIFFRN =
		new CIFCategory(
				"diffrn",
				new String[]{
						"_diffrn_ambient_temperature"
				},
				null);
    /** refine */
	public final static CIFCategory REFINE =
		new CIFCategory(
				"refine",
				new String[]{
						"_refine_ls_R_factor_all",
						"_refine_ls_R_factor_gt",
						"_refine_ls_wR_factor_gt",
						"_refine_ls_wR_factor_ref",
						"_refine_ls_goodness_of_fit_ref",
						"_refine_ls_restrained_S_all",
						"_refine_ls_number_reflns",
						"_refine_ls_hydrogen_treatment"
				},
				null);
    /** atom_sites_solution */
	public final static CIFCategory ATOM_SITES_SOLUTION =
		new CIFCategory(
				"atom_sites_solution",
				new String[]{
						"_atom_sites_solution_hydrogens"
				},
				null);
    /** geom_special */
	public final static CIFCategory GEOM_SPECIAL =
		new CIFCategory(
				"geom_special",
				new String[]{
						"_geom_special_details"
				},
				null);
    /** journal */
	public final static CIFCategory JOURNAL =
		new CIFCategory(
				"journal",
				new String[]{
						"_journal_name_full",
						"_journal_year",
						"_journal_volume",
						"_journal_issue",
						"_journal_page_first",
						"_journal_page_last"
				},
				null);
    /** publ */
	public final static CIFCategory PUBL =
		new CIFCategory(
				"publ",
				new String[]{
						"_publ_section_title"
				},
				null);
    /**  */
	public final static CIFCategory SYMMETRY_EQUIV =
		new CIFCategory(
				"symmetry_equiv",
				new String[]{
				"_symmetry_equiv_pos_as_xyz"},
				null);
    /**  */
	public final static CIFCategory ATOM_TYPE =
		new CIFCategory(
				"atom_type",
				new String[]{
						"_atom_type_symbol",
						"_atom_type_description",
						"_atom_type_scat_dispersion_real",
						"_atom_type_scat_dispersion_imag",
						"_atom_type_scat_source"
				},
		"_atom_type_symbol");
    /**  */
	public final static CIFCategory ATOM_SITE =
		new CIFCategory(
				"atom_site",
				new String[]{
						"_atom_site_label",
						"_atom_site_fract_x",
						"_atom_site_fract_y",
						"_atom_site_fract_z",
						"_atom_site_U_iso_or_equiv",
						"_atom_site_adp_type",
						"_atom_site_calc_flag",
						"_atom_site_refinement_flags",
						"_atom_site_occupancy",
						"_atom_site_disorder_assembly",
						"_atom_site_disorder_group",
						"_atom_site_type_symbol",
						"_atom_site_attached_hydrogens",
						"_atom_site_wyckoff_symbol"
						},
		"atom_site");
    /**  */
	public final static CIFCategory ATOM_SITE_ANISO = 
		new CIFCategory(
				"atom_site_aniso",
				new String[]{
						"_atom_site_aniso_label",
						"_atom_site_aniso_U_11",
						"_atom_site_aniso_U_22",
						"_atom_site_aniso_U_33",
						"_atom_site_aniso_U_12",
						"_atom_site_aniso_U_13",
						"_atom_site_aniso_U_23"
				},
		"atom_site_aniso_label");
    /**  */
	public final static CIFCategory GEOM_BOND =
		new CIFCategory(
				"geom_bond",
				new String[]{
						"_geom_bond_atom_site_label_1",
						"_geom_bond_atom_site_label_2",
						"_geom_bond_site_symmetry_2",
						"_geom_bond_distance",
						"_geom_bond_publ_flag"
				},
		"_geom_bond_atom_site_label_1");
    /**  */
	public final static CIFCategory GEOM_ANGLE =
		new CIFCategory(
				"geom_angle",
				new String[]{
						"_geom_angle_atom_site_label_1",
						"_geom_angle_atom_site_label_2",
						"_geom_angle_atom_site_label_3",
						"_geom_angle_site_symmetry_1",
						"_geom_angle_site_symmetry_3",
						"_geom_angle",
						"_geom_angle_publ_flag"
				},"_geom_angle_atom_site_label_1"
		);
    /**  */
	public final static CIFCategory GEOM_TORSION =
		new CIFCategory(
				"geom_torsion",
				new String[]{
						"_geom_torsion_atom_site_label_1",
						"_geom_torsion_atom_site_label_2",
						"_geom_torsion_atom_site_label_3",
						"_geom_torsion_atom_site_label_4",
						"_geom_torsion",
						"_geom_torsion_publ_flag"
				},"_geom_torsion_atom_site_label_1"
		);
    /**  */
	public final static CIFCategory PUBL_AUTHOR =
		new CIFCategory(
				"publ_author",
				new String[]{
						"_publ_author_name",
						"_publ_author_address"
				},
				"_publ_author_name"
		);
	
	private String name;
	private List<String> memberList;
	private String loopKey;
	private List<CIFEntry> entryList;
	
	/** create empty category.
	 * 
	 * @param name
	 */
	public CIFCategory(String name) {
		this.name = name;
		memberList = new ArrayList<String>();
		entryList = new ArrayList<CIFEntry>();
	}
	/** create a category.
	 *
	 * @param name without leading underscore
	 * @param members with leading underscores
	 * @param loopKey if not null is loop key
	 */
	public CIFCategory(final String name, final String[] members, final String loopKey) {
		this(name);
		for (final String m : members) {
			memberList.add(m);
		}
		this.setLoopKey(loopKey);
	}
	/** does category contain a given name.
	 *
	 * @param name
	 * @return true if it does
	 */
	public boolean contains(final String name) {
		boolean contains = false;
		for (final String member : memberList) {
			if (member.equalsIgnoreCase(name)) {
				contains = true;
				break;
			}
		}
		return contains;
	}
	
	/**
	 * @return the loopKey
	 */
	public String getLoopKey() {
		return loopKey;
	}
	/**
	 * @param loopKey the loopKey to set
	 */
	public void setLoopKey(String loopKey) {
		this.loopKey = loopKey;
	}
	/**
	 * @param memberList the memberList to set
	 */
	public void setMemberList(List<String> memberList) {
		this.memberList = memberList;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/** get list of cif names.
	 * case-insensitive
	 * @return the list of names
	 */
	public List<String> getMemberList() {
		return memberList;
	}
    /** does category match against list of categoryNames.
     * if this contains any member of categoryNameList return true; 
     * @param categoryNameList
     * @return true if matches
     */
	public boolean matches(final List<String> categoryNameList) {
		boolean contains = false;
		for (final String name : categoryNameList) {
			if (contains(name)) {
				contains = true;
			}
		}
		return contains;
	}
    /** get category name.
     * @return name
     */
	public String getName() {
		return name;
	}
	/**
	 * @return the entryList
	 */
	public List<CIFEntry> getEntryList() {
		return entryList;
	}
	/**
	 * @param entryList the entryList to set
	 */
	public void setEntryList(List<CIFEntry> entryList) {
		this.entryList = entryList;
	}

	/** add entry.
	 * updates memberlist
	 * @param entry
	 */
	public void add(CIFEntry entry) {
//		String categoryS = entry.getCategoryName();
//		if (categoryS != )
		memberList.add(entry.getId());
		entryList.add(entry);
	}
}
