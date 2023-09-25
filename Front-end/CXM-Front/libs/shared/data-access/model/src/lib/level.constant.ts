export class LevelConstant{
  public static SPECIFIC = 'specific';
  public static CLIENT = 'client';
  public static DIVISION = 'division';
  public static SERVICE = 'service';
  public static USER = 'user';
  public static OWNER = 'owner';

  public static CASE1 = [LevelConstant.OWNER, LevelConstant.USER];
  public static CASE2 = [LevelConstant.SERVICE, LevelConstant.DIVISION, LevelConstant.CLIENT];
}
