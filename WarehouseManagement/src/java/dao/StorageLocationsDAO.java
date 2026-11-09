/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.StorageLocations;
import utils.DbContext;

public class StorageLocationsDAO extends DbContext {
    public StorageLocations getAvailableQuarantineLocation() {
        String sql = "SELECT TOP 1 LocationId, ZoneName, ShelfCode, BinCode, LocationType, Status "
                + "FROM StorageLocations WHERE Status = 'Available' AND ZoneName LIKE N'%Cách ly%' "
                + "ORDER BY LocationId";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                StorageLocations location = new StorageLocations();
                location.setLocationId(rs.getInt("LocationId"));
                location.setZoneName(rs.getString("ZoneName"));
                location.setShelfCode(rs.getString("ShelfCode"));
                location.setBinCode(rs.getString("BinCode"));
                location.setLocationType(rs.getString("LocationType"));
                location.setStatus(rs.getString("Status"));
                return location;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<StorageLocations> getAvailableLocations() {
        List<StorageLocations> locations = new ArrayList<>();
        String sql = "SELECT LocationId, ZoneName, ShelfCode, BinCode, LocationType, Status "
                + "FROM StorageLocations WHERE Status = 'Available' ORDER BY ZoneName, ShelfCode, BinCode";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                StorageLocations location = new StorageLocations();
                location.setLocationId(rs.getInt("LocationId"));
                location.setZoneName(rs.getString("ZoneName"));
                location.setShelfCode(rs.getString("ShelfCode"));
                location.setBinCode(rs.getString("BinCode"));
                location.setLocationType(rs.getString("LocationType"));
                location.setStatus(rs.getString("Status"));
                locations.add(location);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return locations;
    }

    public boolean isAvailableLocation(int locationId) {
        String sql = "SELECT 1 FROM StorageLocations WHERE LocationId = ? AND Status = 'Available'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, locationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
