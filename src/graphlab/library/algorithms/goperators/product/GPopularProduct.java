// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU Lesser General Public License (LGPL): http://www.gnu.org/licenses/

package graphlab.library.algorithms.goperators.product;

import graphlab.library.BaseVertex;

//todo: doc needed (rostam)
public class GPopularProduct extends GProduct {
    public boolean compare(BaseVertex v1OfFirstG, BaseVertex v2OfFirstG, BaseVertex v1OfSecondG, BaseVertex v2OfSecondG) {
        return g1.isEdge(v1OfFirstG, v2OfFirstG)
                && g2.isEdge(v1OfSecondG, v2OfSecondG);
    }
}
