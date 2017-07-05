package com.zmap.login.bean;

public class SpinnerData
{
  private String text;
  private String value;

  public SpinnerData()
  {
  }

  public SpinnerData(String paramString1, String paramString2)
  {
    this.value = paramString1;
    this.text = paramString2;
  }

  public String getText()
  {
    return this.text;
  }

  public String getValue()
  {
    return this.value;
  }

  public void setText(String paramString)
  {
    this.text = paramString;
  }

  public void setValue(String paramString)
  {
    this.value = paramString;
  }

  public String toString()
  {
    return this.text;
  }
}