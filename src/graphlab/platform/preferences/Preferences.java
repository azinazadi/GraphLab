// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
package graphlab.platform.preferences;

import graphlab.platform.attribute.NotifiableAttributeSetImpl;
import graphlab.platform.core.BlackBoard;
import graphlab.platform.core.exception.ExceptionHandler;
import graphlab.platform.lang.ArrayX;
import graphlab.platform.preferences.lastsettings.StorableOnExit;
import graphlab.platform.preferences.lastsettings.UserModifiableProperty;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Rouzbeh Ebrahimi
 */
public class Preferences implements StorableOnExit {

    public static final String NAME = "__Prefferences";

    static {
        categories = new HashMap<Object, String>();
    }

    public static HashMap<Object, String> categories;

    public HashSet<NotifiableAttributeSetImpl> setOfAttributes = new HashSet<NotifiableAttributeSetImpl>();
    public HashSet<AbstractPreference> set = new HashSet<AbstractPreference>();

    public void putNewSetOfAttributes(AbstractPreference ap) {
        setOfAttributes.add(ap.attributeSet);
        set.add(ap);
    }

    public Preferences(BlackBoard bb) {
        bb.setData("Preferences", this);
        GraphPreferences.pref = this;

    }

    public void retrieveEveryItem() {
        HashSet<Object> objects = SETTINGS.getRegisteredObjects();
        HashMap<String, GraphPreferences> gPrefs = new HashMap<String, GraphPreferences>();
        for (Object o : objects) {
            if (o instanceof UserDefinedEligiblity) {
                UserDefinedEligiblity um = (UserDefinedEligiblity) o;
                GraphPreferences gp = um.GraphPrefFactory();
                gp.defineAttributes(um.defineEligibleValuesForSettings(new HashMap<Object, ArrayX>()));
            } else {
                String key = Preferences.categories.get(o);
                if (!gPrefs.containsKey(key)) {
                    GraphPreferences gp = new GraphPreferences(Preferences.categories.get(o), o, Preferences.categories.get(o));
                    HashMap<Object, Object> eligiblesValues = detectEligibleValues(gPrefs, o);
                    gp.defineAttributes(eligiblesValues, true);
                    gPrefs.put(key, gp);
                } else {
                    GraphPreferences gpref = gPrefs.get(key);
                    gpref.addObject(o);
                    HashMap<Object, Object> eligiblesValues = detectEligibleValues(gPrefs, o);
                    gpref.defineAttributes(eligiblesValues, true);
                }
            }
        }
    }

    private HashMap<Object, Object> detectEligibleValues(HashMap<String, GraphPreferences> gprefs, Object o) {
        HashMap<Object, Object> eligiblesValues = new HashMap<Object, Object>();
        HashMap<Object, Object> exceptionalEligiblesValues = new HashMap<Object, Object>();
        for (Field f : o.getClass().getFields()) {
            UserModifiableProperty anot = f.getAnnotation(UserModifiableProperty.class);
            if (anot != null) {
                try {
                    if (anot.obeysAncestorCategory()) {
                        eligiblesValues.put(anot.displayName(), f.get(o));
                    } else {
                        if (gprefs.containsKey(anot.category())) {
                            GraphPreferences gp = gprefs.get(anot.category());
                            gp.addObject(o);
                            exceptionalEligiblesValues.put(anot.displayName(), f.get(o));
                            gp.defineAttributes(exceptionalEligiblesValues, true);

                        } else {
                            GraphPreferences gp = new GraphPreferences(anot.category(), o, anot.category());
                            exceptionalEligiblesValues.put(anot.displayName(), f.get(o));
                            gp.defineAttributes(exceptionalEligiblesValues, true);
                            gprefs.put(anot.category(), gp);
                        }
                    }
                } catch (IllegalAccessException e) {
                    ExceptionHandler.catchException(e);
                }
            }
        }
        return eligiblesValues;
    }
}
