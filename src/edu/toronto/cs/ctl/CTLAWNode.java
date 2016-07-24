package edu.toronto.cs.ctl;

/**
 ** This is a universal strong until CTL node.
 **/
public class CTLAWNode extends CTLBinaryNode
{

  /**
   ** Construct a universal strong until CTL node using two other
   ** nodes as its children.
   **/
  protected CTLAWNode (CTLNode phi, CTLNode psi)
  {
    super (phi, psi, null);
  }

  protected CTLAWNode (CTLNode phi, CTLNode psi, CTLNode[] fairness)
  {
    super (phi, psi, fairness);
  }
  
  public String toString ()
  {
    return "A[" + getLeft () + " W " + getRight () + "]";
  }

  public Object accept (CTLVisitor v, Object s)
  {
    return v.visitAWNode (this, s);
  }


}
