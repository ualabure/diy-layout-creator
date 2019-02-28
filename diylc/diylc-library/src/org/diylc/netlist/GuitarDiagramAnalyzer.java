/*

    DIY Layout Creator (DIYLC).
    Copyright (c) 2009-2018 held jointly by the individual authors.

    This file is part of DIYLC.

    DIYLC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    DIYLC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with DIYLC.  If not, see <http://www.gnu.org/licenses/>.

*/
package org.diylc.netlist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.diylc.common.INetlistAnalyzer;
import org.diylc.components.electromechanical.ClosedJack1_4;
import org.diylc.components.electromechanical.OpenJack1_4;
import org.diylc.components.guitar.HumbuckerPickup;
import org.diylc.components.guitar.JazzBassPickup;
import org.diylc.components.guitar.P90Pickup;
import org.diylc.components.guitar.PBassPickup;
import org.diylc.components.guitar.SingleCoilPickup;
import org.diylc.components.passive.PotentiometerPanel;

public class GuitarDiagramAnalyzer extends NetlistAnalyzer implements INetlistAnalyzer {
  
  private static Set<String> JACK_TYPES = new HashSet<String>();
  private static Set<String> POT_TYPES = new HashSet<String>();
  private static Set<String> PICKUP_TYPES = new HashSet<String>();
  static {
    JACK_TYPES.add(OpenJack1_4.class.getCanonicalName());
    JACK_TYPES.add(ClosedJack1_4.class.getCanonicalName());
    
    POT_TYPES.add(PotentiometerPanel.class.getCanonicalName());
    
    PICKUP_TYPES.add(SingleCoilPickup.class.getCanonicalName());
    PICKUP_TYPES.add(HumbuckerPickup.class.getCanonicalName());
    PICKUP_TYPES.add(P90Pickup.class.getCanonicalName());
    PICKUP_TYPES.add(JazzBassPickup.class.getCanonicalName());
    PICKUP_TYPES.add(PBassPickup.class.getCanonicalName());
  }

  @Override
  public String getName() {  
    return "Guitar Diagrams";
  }
  
  @Override
  public String getIconName() {
    return "Guitar";
  }
    
  public List<Summary> summarize(List<Netlist> netlists, Node preferredOutput) throws TreeException {    
    List<Summary> summaries = new ArrayList<Summary>();
    for (Netlist n : netlists)
      summaries.add(summarize(n, preferredOutput));
    
    return summaries;
  }
  
  private Summary summarize(Netlist netlist, Node preferredOutput) throws TreeException {
    List<String> notes = new ArrayList<String>();

    Tree tree = constructTree(netlist);
    
    return new Summary(netlist, notes, tree);
  }

  @Override
  public Tree constructTree(Netlist netlist) throws TreeException {
    List<Node> jackTipNodes = find(JACK_TYPES, "Tip", netlist);
    List<Node> jackSleeveNodes = find(JACK_TYPES, "Sleeve", netlist);
    
    if (jackTipNodes.size() == 0 || jackSleeveNodes.size() == 0) {
      throw new TreeException("Could not find anything connected to a jack tip terminal.");
    } else if (jackTipNodes.size() > 1 || jackSleeveNodes.size() > 1) {
      throw new TreeException("Multiple jacks found, could not proceed.");
    } else {      
      Node jackTip = jackTipNodes.get(0);
      Node jackSleeve = jackSleeveNodes.get(0);      
      return constructTreeBetween(netlist, jackTip, jackSleeve);
    }
  }
}