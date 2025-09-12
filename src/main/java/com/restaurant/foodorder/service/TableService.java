package com.restaurant.foodorder.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.restaurant.foodorder.dto.APIResponse;
import com.restaurant.foodorder.dto.TableAndStatus;
import com.restaurant.foodorder.model.Table;
import com.restaurant.foodorder.repo.TableRepo;
import com.restaurant.foodorder.repo.TempOrderRepo;

@Service
public class TableService {
    private final TableRepo tableRepository;
    private final TempOrderRepo TempOrderRepo;

    public TableService(TableRepo tableRepository, TempOrderRepo TempOrderRepo) {
        this.tableRepository = tableRepository;
        this.TempOrderRepo = TempOrderRepo;
    }

    public APIResponse<List<TableAndStatus>> getAllTables() {
        List<Table> tables = tableRepository.findAll();
        List<TableAndStatus> tableAndStatus = new ArrayList<>();
        tables.forEach(table -> {
            boolean hasTempOrder = TempOrderRepo.existsById(table.getId());
            if (hasTempOrder) {
                TableAndStatus tas = new TableAndStatus();
                tas.setTableId(table.getId());
                tas.setStatus("Occupied");
                tas.setTotalPrice(TempOrderRepo.findById(table.getId()).get().getTotalPrice());
                tableAndStatus.add(tas);
            } else {
                TableAndStatus tas = new TableAndStatus();
                tas.setTableId(table.getId());
                tas.setStatus("Empty");
                tas.setTotalPrice(0);
                tableAndStatus.add(tas);
            }
        });

        return APIResponse.<List<TableAndStatus>>builder()
                .statusCode(200)
                .message("Tables retrieved successfully")
                .data(tableAndStatus)
                .build();
    }

}
