package com.gpl.rpg.AndorsTrail.resource.tiles;

import java.util.HashSet;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.ImageView;

import com.gpl.rpg.AndorsTrail.AndorsTrailPreferences;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.ActorConditionType;
import com.gpl.rpg.AndorsTrail.model.actor.Monster;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.Inventory;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer;
import com.gpl.rpg.AndorsTrail.model.item.ItemType;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer.ItemEntry;
import com.gpl.rpg.AndorsTrail.model.map.LayeredTileMap;
import com.gpl.rpg.AndorsTrail.model.map.MonsterSpawnArea;
import com.gpl.rpg.AndorsTrail.model.map.PredefinedMap;

public final class TileManager {
	public static final int CHAR_HERO = 1;
	public static final int iconID_selection_red = 2;
	public static final int iconID_selection_yellow = 3;
	public static final int iconID_attackselect = iconID_selection_red;
	public static final int iconID_moveselect = iconID_selection_yellow;
	public static final int iconID_groundbag = 4;
	public static final int iconID_boxopened = 5;
	public static final int iconID_boxclosed = 6;
	public static final int iconID_shop = iconID_groundbag;
	public static final int iconID_selection_blue = 7;
	public static final int iconID_selection_purple = 8;
	public static final int iconID_selection_green = 9;

    private float density;
	public int tileSize;

	public int viewTileSize;
    public float scale;

    
	public final TileCache tileCache = new TileCache();
	public final TileCollection preloadedTiles = new TileCollection(72);
	public TileCollection currentMapTiles;
	private final HashSet<Integer> preloadedTileIDs = new HashSet<Integer>();
	
	
	public TileCollection loadTilesFor(ItemContainer container, Resources r) {
		HashSet<Integer> iconIDs = new HashSet<Integer>();
		for(ItemEntry i : container.items) {
			iconIDs.add(i.itemType.iconID);
		}
		return tileCache.loadTilesFor(iconIDs, r);
	}
	
	public TileCollection loadTilesFor(Inventory inventory, Resources r) {
		HashSet<Integer> iconIDs = new HashSet<Integer>();
		for(ItemEntry i : inventory.items) {
			iconIDs.add(i.itemType.iconID);
		}
		for(ItemType t : inventory.wear) {
			if (t != null) iconIDs.add(t.iconID);
		}
		return tileCache.loadTilesFor(iconIDs, r);
	}
	
	public TileCollection loadTilesFor(PredefinedMap map, LayeredTileMap tileMap, WorldContext world, Resources r) {
		HashSet<Integer> iconIDs = new HashSet<Integer>();
		for(MonsterSpawnArea a : map.spawnAreas) {
			for(String monsterTypeID : a.monsterTypeIDs) {
				iconIDs.add(world.monsterTypes.getMonsterType(monsterTypeID).iconID);
			}
		}
		iconIDs.addAll(tileMap.usedTileIDs);
		
		TileCollection result = tileCache.loadTilesFor(iconIDs, r);
		for(int i : preloadedTileIDs) {
			result.setBitmap(i, preloadedTiles.getBitmap(i));
		}
		return result;
	}
	
	public void setDensity(Resources r) {
		density = r.getDisplayMetrics().density;
		tileSize = (int) (32 * density);
	}
	
	public void updatePreferences(AndorsTrailPreferences prefs) {
		scale = prefs.scalingFactor;
        viewTileSize = (int) (tileSize * prefs.scalingFactor);
	}
	
	
	
	public void setImageViewTile(ImageView imageView, Monster monster) { setImageViewTileForMonster(imageView, monster.actorTraits.iconID); }
	public void setImageViewTile(ImageView imageView, Player player) { setImageViewTileForPlayer(imageView, player.actorTraits.iconID); }
	public void setImageViewTileForMonster(ImageView imageView, int iconID) { imageView.setImageBitmap(currentMapTiles.getBitmap(iconID)); }
	public void setImageViewTileForPlayer(ImageView imageView, int iconID) { imageView.setImageBitmap(preloadedTiles.getBitmap(iconID)); }
	public void setImageViewTile(ImageView imageView, ActorConditionType conditionType) { imageView.setImageBitmap(preloadedTiles.getBitmap(conditionType.iconID)); }
	public void setImageViewTileForUIIcon(ImageView imageView, int iconID) { imageView.setImageBitmap(preloadedTiles.getBitmap(iconID)); }

	public void setImageViewTileForSingleItemType(ImageView imageView, ItemType itemType, Resources r) {
		final Bitmap icon = tileCache.loadSingleTile(itemType.iconID, r);
		setImageViewTile(imageView, itemType, icon);
	}
	public void setImageViewTile(ImageView imageView, ItemType itemType, TileCollection itemTileCollection) {
		final Bitmap icon = itemTileCollection.getBitmap(itemType.iconID);
		setImageViewTile(imageView, itemType, icon);
	}
	private void setImageViewTile(ImageView imageView, ItemType itemType, Bitmap icon) {
		final int overlayIconID = itemType.getOverlayTileID();
		if (overlayIconID != -1) {
			imageView.setImageDrawable(
				new LayerDrawable(new Drawable[] {
					new BitmapDrawable(preloadedTiles.getBitmap(overlayIconID))
					,new BitmapDrawable(icon)
				})
			);
		} else {
			imageView.setImageBitmap(icon);
		}
	}


	public void loadPreloadedTiles(Resources r) {
		int maxTileID = tileCache.getMaxTileID();
        for(int i = TileManager.CHAR_HERO; i <= maxTileID; ++i) {
        	preloadedTileIDs.add(i);
        }
        tileCache.loadTilesFor(preloadedTileIDs, r, preloadedTiles);
	}
}